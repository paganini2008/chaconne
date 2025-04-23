package com.github.chaconne;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.cronsmith.scheduler.ErrorHandler;

/**
 * 
 * @Description: ClockWheelScheduler
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public class ClockWheelScheduler {

    private static final Logger log = LoggerFactory.getLogger(ClockWheelScheduler.class);

    public ClockWheelScheduler() {
        this(new DefaultExecutorServiceFactory());
    }

    public ClockWheelScheduler(ExecutorServiceFactory executorServiceFactory) {
        this.schedulerThreads = executorServiceFactory.getSchedulerThreads();
        this.workerThreads = executorServiceFactory.getWorkerThreads();
        this.executorServiceFactory = executorServiceFactory;
    }

    private final ScheduledExecutorService schedulerThreads;
    private final ExecutorService workerThreads;
    private final ExecutorServiceFactory executorServiceFactory;
    private TaskManager taskManager = new InMemoryTaskManager();
    private UpcomingTaskQueue taskQueue = new InMemoryTaskQueue();
    private ZoneId zoneId = ZoneId.of("UTC");
    private List<TaskListener> taskListeners = new ArrayList<>();
    private ErrorHandler errorHandler = new LoggingErrorHandler();
    private final AtomicBoolean started = new AtomicBoolean();
    private OneOffTimer consumer;

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public UpcomingTaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(UpcomingTaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public List<TaskListener> getTaskListeners() {
        return taskListeners;
    }

    public void setTaskListeners(List<TaskListener> taskListeners) {
        this.taskListeners = taskListeners;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void schedule(Task task, String initialParameter) {
        taskManager.saveTask(task, initialParameter);
        preloadUpcomingTasks(task.getTaskId());
    }

    public void pause(Task task) {
        TaskStatus taskStatus = taskManager.getTaskStatus(task.getTaskId());
        if (taskStatus == TaskStatus.SCHEDULED || taskStatus == TaskStatus.STANDBY) {
            taskManager.setTaskStatus(task.getTaskId(), TaskStatus.PAUSED);
        }
    }

    public void resume(Task task) {
        if (taskManager.getTaskStatus(task.getTaskId()) == TaskStatus.PAUSED) {
            preloadUpcomingTasks(task.getTaskId());
        }
    }

    public void cancel(Task task) {
        TaskStatus taskStatus = taskManager.getTaskStatus(task.getTaskId());
        if (taskStatus == TaskStatus.SCHEDULED || taskStatus == TaskStatus.STANDBY) {
            taskManager.setTaskStatus(task.getTaskId(), TaskStatus.CANCELED);
        }
    }

    public void remove(Task task) {
        if (taskManager.getTaskStatus(task.getTaskId()) == TaskStatus.CANCELED) {
            taskManager.removeTask(task.getTaskId());
        }
    }

    public boolean isStarted() {
        return started.get();
    }

    private boolean preloadUpcomingTasks(TaskId taskId) {
        boolean preloaded = false;
        LocalDateTime now = getNow();
        LocalDateTime nextFiredDateTime = taskManager.computeNextFiredDateTime(taskId, now);
        if (nextFiredDateTime == null) {
            taskManager.setTaskStatus(taskId, TaskStatus.FINISHED);
            TaskDetail taskDetail = taskManager.getTaskDetail(taskId);
            taskListeners.forEach(l -> {
                l.onTaskFinished(taskDetail);
            });
        } else {
            if (taskQueue.addTask(nextFiredDateTime, taskId)) {
                if (log.isTraceEnabled()) {
                    log.trace("TaskId '{}' will be triggered at {}", taskId, nextFiredDateTime);
                }
            }
            LocalDateTime duration = now.plus(1L, ChronoUnit.MINUTES);
            List<LocalDateTime> firedDateTimes =
                    taskManager.findNextFiredDateTimes(taskId, now, duration);
            if (firedDateTimes != null && firedDateTimes.size() > 0) {
                for (LocalDateTime ldt : firedDateTimes) {
                    if (taskQueue.addTask(ldt, taskId)) {
                        if (log.isTraceEnabled()) {
                            log.trace("TaskId '{}' will be triggered at {}", taskId, ldt);
                        }
                    }
                }
                taskManager.setTaskStatus(taskId, TaskStatus.SCHEDULED);
                TaskDetail taskDetail = taskManager.getTaskDetail(taskId);
                taskListeners.forEach(l -> {
                    l.onTaskScheduled(taskDetail);
                });
                preloaded = true;
            } else {
                taskManager.setTaskStatus(taskId, TaskStatus.STANDBY);
            }
        }
        return preloaded;
    }

    public void start() {
        if (consumer == null && !started.get()) {
            started.set(true);
            consumer = new OneOffTimer(1, 1, TimeUnit.SECONDS, new TaskQueueLoop());
            consumer.start(false, true);
            log.info("ClockWheelScheduler is started.");
        }
    }

    public void close() {
        if (!started.get()) {
            return;
        }
        if (consumer != null) {
            consumer.close();
            consumer = null;
        }
        if (executorServiceFactory.isAutoClosed()) {
            executorServiceFactory.shutdown(workerThreads);
            executorServiceFactory.shutdown(schedulerThreads);
        }
        started.set(false);
        log.info("ClockWheelScheduler is closed.");
    }

    private LocalDateTime getNow() {
        return LocalDateTime.now(zoneId).withNano(0);
    }

    private class TaskQueueLoop implements OneOffTask {

        @Override
        public boolean execute() {
            final LocalDateTime firedDateTime = getNow();
            Collection<TaskId> taskIds = taskQueue.matchTaskIds(firedDateTime);
            if (log.isTraceEnabled()) {
                log.trace("FiredDateTime: {}, TaskIds' size: {}, TaskQueueLength: {}",
                        firedDateTime, taskIds.size(), taskQueue.length());
            }
            if (taskIds != null && taskIds.size() > 0) {
                taskIds.forEach(taskId -> {

                    workerThreads.execute(() -> preloadUpcomingTasks(taskId));
                    TaskDetail taskDetail = taskManager.getTaskDetail(taskId);
                    if (taskDetail != null && !taskDetail.isUnavailable()) {
                        taskManager.setTaskStatus(taskId, TaskStatus.RUNNING);
                        TaskProxy taskProxy = new TaskProxy(firedDateTime, taskDetail,
                                workerThreads, taskListeners, errorHandler);
                        Throwable reason = null;
                        int retryCount = 0;
                        do {
                            try {
                                taskProxy.getProxyObject()
                                        .execute(taskManager.getInitialParameter(taskId));
                                break;
                            } catch (Throwable e) {
                                reason = e;
                            }
                        } while (retryCount++ < taskDetail.getTask().getMaxRetryCount()
                                && reason != null);
                        taskManager.setTaskStatus(taskId, TaskStatus.STANDBY);
                    }
                });
            }
            return started.get();
        }

        @Override
        public boolean onError(Throwable e) {
            errorHandler.onHandleScheduler(e);
            return true;
        }
    }
}
