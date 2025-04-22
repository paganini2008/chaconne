package com.github.chaconne.cluster;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

/**
 * 
 * @Description: LocalTaskMemberManager
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public class LocalTaskMemberManager implements TaskMemberManager, SmartApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(RemoteTaskMemberManager.class);

    private final Queue<TaskMember> schedulers = new PriorityBlockingQueue<>();
    private final List<TaskMember> executors = new CopyOnWriteArrayList<>();

    @Override
    public void addExecutor(TaskMember taskMember) {
        if (taskMember != null && !executors.contains(taskMember)) {
            executors.add(taskMember);
        }
    }

    @Override
    public Queue<TaskMember> getSchedulers() {
        return schedulers;
    }

    @Override
    public List<TaskMember> getExecutors() {
        return executors;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof TaskMemberAddedEvent) {
            TaskMember taskMember = ((TaskMemberAddedEvent) event).getTaskMember();
            if (!schedulers.contains(taskMember)) {
                schedulers.add(taskMember);
            }
            if (!executors.contains(taskMember)) {
                executors.add(taskMember);
            }
        } else if (event instanceof TaskMemberRemovedEvent) {
            TaskMember taskMember = ((TaskMemberRemovedEvent) event).getTaskMember();
            schedulers.remove(taskMember);
            executors.remove(taskMember);
        }
        if (log.isInfoEnabled()) {
            log.info("Task schedulers: {}", schedulers.toString());
            log.info("Task executors: {}", executors.toString());
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(TaskMemberAddedEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }

}
