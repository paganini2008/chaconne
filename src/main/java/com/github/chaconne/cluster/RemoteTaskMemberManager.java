package com.github.chaconne.cluster;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import com.hazelcast.collection.IList;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: RemoteTaskMemberManager
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskMemberManager implements TaskMemberManager, SmartApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(RemoteTaskMemberManager.class);

    public RemoteTaskMemberManager(HazelcastInstance hazelcastInstance) {
        this.executors = hazelcastInstance.getList("REMOTE_TASK_EXECUTOR");
    }

    private final Queue<TaskMember> schedulers = new PriorityQueue<>();
    private final IList<TaskMember> executors;

    public void addTaskExecutor(TaskMember taskMember) {
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
        } else if (event instanceof TaskMemberRemovedEvent) {
            TaskMember taskMember = ((TaskMemberRemovedEvent) event).getTaskMember();
            schedulers.remove(taskMember);
        }
        if (log.isInfoEnabled()) {
            log.info("Task schedulers: {}", schedulers.toString());
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(TaskMemberAddedEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }



}
