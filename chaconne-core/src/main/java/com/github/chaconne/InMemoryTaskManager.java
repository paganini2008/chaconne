package com.github.chaconne;

import static com.github.chaconne.Settings.DEFAULT_ZONE_ID;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import com.github.cronsmith.cron.CronExpression;

/**
 * 
 * @Description: InMemoryTaskManager
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public class InMemoryTaskManager implements TaskManager {

    private final Map<TaskId, InMemoryTaskDetail> taskStore = new ConcurrentHashMap<>();

    private static class InMemoryTaskDetail implements TaskDetail {

        private final Task task;
        private final String initialParameter;
        private TaskStatus taskStatus;
        private LocalDateTime previousFiredDateTime;
        private LocalDateTime nextFiredDateTime;
        private LocalDateTime lastModified;

        InMemoryTaskDetail(Task task, String initialParameter, TaskStatus taskStatus) {
            this.task = task;
            this.initialParameter = initialParameter;
            this.taskStatus = taskStatus;
            this.lastModified = LocalDateTime.now(DEFAULT_ZONE_ID);
        }

        public Task getTask() {
            return task;
        }

        public String getInitialParameter() {
            return initialParameter;
        }

        public TaskStatus getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(TaskStatus taskStatus) {
            this.taskStatus = taskStatus;
            this.lastModified = LocalDateTime.now(DEFAULT_ZONE_ID);
        }

        public void setPreviousFiredDateTime(LocalDateTime previousFiredDateTime) {
            this.previousFiredDateTime = previousFiredDateTime;
        }

        public LocalDateTime getNextFiredDateTime() {
            return nextFiredDateTime;
        }

        public void setNextFiredDateTime(LocalDateTime nextFiredDateTime) {
            this.nextFiredDateTime = nextFiredDateTime;
        }

        public LocalDateTime getPreviousFiredDateTime() {
            return previousFiredDateTime;
        }

        public LocalDateTime getLastModified() {
            return lastModified;
        }

        public void setLastModified(LocalDateTime lastModified) {
            this.lastModified = lastModified;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append("Task Id: ").append(task.getTaskId()).append(", Task Status: ")
                    .append(taskStatus).append(", Previous Fired: ").append(previousFiredDateTime)
                    .append(", Next Fired: ").append(nextFiredDateTime);
            return str.toString();
        }

    }

    @Override
    public TaskDetail saveTask(Task task, String initialParameter) {
        task.getCronExpression().sync();
        taskStore.put(task.getTaskId(),
                new InMemoryTaskDetail(task,
                        StringUtils.isNotBlank(initialParameter) ? initialParameter
                                : task.getInitialParameter(),
                        TaskStatus.STANDBY));
        return taskStore.get(task.getTaskId());
    }

    @Override
    public List<LocalDateTime> findNextFiredDateTimes(TaskId taskId, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        InMemoryTaskDetail taskDetail = taskStore.get(taskId);
        if (taskDetail.isUnavailable()) {
            return Collections.emptyList();
        }
        CronExpression cronExpression = taskDetail.getTask().getCronExpression();
        List<LocalDateTime> results = cronExpression.list(startDateTime, endDateTime);
        return results;
    }

    @Override
    public TaskDetail removeTask(TaskId taskId) {
        return taskStore.remove(taskId);
    }

    @Override
    public String getInitialParameter(TaskId taskId) {
        return taskStore.containsKey(taskId) ? taskStore.get(taskId).getInitialParameter() : null;
    }

    @Override
    public int getTaskCount(String taskGroup, String taskName, String taskClass) {
        return (int) taskStore.entrySet().stream()
                .filter(e -> StringUtils.isBlank(taskGroup)
                        || e.getKey().getGroup().equals(taskGroup))
                .filter(e -> StringUtils.isBlank(taskName)
                        || e.getKey().getName().contains(taskName))
                .filter(e -> StringUtils.isBlank(taskClass)
                        || e.getValue().getTask().getClass().getName().contains(taskClass))
                .count();
    }

    @Override
    public List<TaskDetail> findTaskDetails(String taskGroup, String taskName, String taskClass,
            int limit, int offset) {
        return taskStore.entrySet().stream()
                .filter(e -> StringUtils.isBlank(taskGroup)
                        || e.getKey().getGroup().equals(taskGroup))
                .filter(e -> StringUtils.isBlank(taskName)
                        || e.getKey().getName().contains(taskName))
                .filter(e -> StringUtils.isBlank(taskClass)
                        || e.getValue().getTask().getClass().getName().contains(taskClass))
                .skip(offset).limit(limit).map(e -> e.getValue()).collect(Collectors.toList());
    }

    @Override
    public boolean hasTask(TaskId taskId) {
        return taskStore.containsKey(taskId);
    }

    @Override
    public void setTaskStatus(TaskId taskId, TaskStatus status) {
        if (taskStore.containsKey(taskId)) {
            taskStore.get(taskId).setTaskStatus(status);
            taskStore.get(taskId).setLastModified(LocalDateTime.now(DEFAULT_ZONE_ID));
        }
    }

    @Override
    public TaskStatus getTaskStatus(TaskId taskId) {
        return taskStore.containsKey(taskId) ? taskStore.get(taskId).getTaskStatus() : null;
    }

    @Override
    public TaskDetail getTaskDetail(TaskId taskId, boolean thrown) {
        TaskDetail taskDetail = taskStore.get(taskId);
        if (taskDetail != null) {
            return taskDetail;
        } else if (thrown) {
            throw new TaskDetailNotFoundException();
        }
        return null;
    }

    @Override
    public List<TaskId> findUpcomingTasksBetween(LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return taskStore.entrySet().stream().filter(e -> {
            if (e.getValue().getTaskStatus() != TaskStatus.STANDBY) {
                return false;
            }
            LocalDateTime nextFiredTime = e.getValue().getNextFiredDateTime();
            return (nextFiredTime.isAfter(startDateTime) || nextFiredTime.isEqual(startDateTime))
                    && (nextFiredTime.isBefore(endDateTime));
        }).map(e -> e.getKey()).collect(Collectors.toList());
    }

    @Override
    public LocalDateTime computeNextFiredDateTime(TaskId taskId,
            LocalDateTime previousFiredDateTime) {
        if (taskStore.containsKey(taskId)) {
            InMemoryTaskDetail taskDetail = taskStore.get(taskId);
            LocalDateTime nextFiredDateTime = taskDetail.getTask().getCronExpression()
                    .getNextFiredDateTime(previousFiredDateTime);
            taskDetail.setPreviousFiredDateTime(taskDetail.getNextFiredDateTime());
            taskDetail.setNextFiredDateTime(nextFiredDateTime);
            return nextFiredDateTime;
        }
        return null;
    }

    @Override
    public void restoreTasks(TaskRestoreHandler recoveryHandler) throws ChaconneException {}

}
