package com.github.chaconne.cluster;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.SmartApplicationListener;
import com.github.chaconne.common.TaskMember;
import com.hazelcast.collection.IList;
import com.hazelcast.collection.ItemEvent;
import com.hazelcast.collection.ItemListener;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: RemoteTaskMemberManager
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskMemberManager implements TaskMemberManager, SmartApplicationListener,
        ItemListener<TaskMember>, ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(RemoteTaskMemberManager.class);

    public RemoteTaskMemberManager(HazelcastInstance hazelcastInstance) {
        this.executors = hazelcastInstance.getList("REMOTE_TASK_EXECUTOR");
        this.executors.addItemListener(this, true);
    }

    private final Queue<TaskMember> schedulers = new PriorityBlockingQueue<>();
    private final IList<TaskMember> executors;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
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
            TaskMemberAddedEvent addedEvent = (TaskMemberAddedEvent) event;
            if (addedEvent.getClusterMode() == ClusterMode.LOCAL) {
                TaskMember taskMember = addedEvent.getTaskMember();
                if (!schedulers.contains(taskMember)) {
                    schedulers.add(taskMember);
                }
            }
        } else if (event instanceof TaskMemberRemovedEvent) {
            TaskMemberRemovedEvent removedEvent = (TaskMemberRemovedEvent) event;
            TaskMember taskMember = removedEvent.getTaskMember();
            if (removedEvent.getClusterMode() == ClusterMode.LOCAL) {
                schedulers.remove(taskMember);
            } else {
                executors.remove(taskMember);
            }
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

    @Override
    public void addExecutor(TaskMember taskMember) {
        if (taskMember != null && !executors.contains(taskMember)) {
            executors.add(taskMember);
        }
    }

    @Override
    public void itemAdded(ItemEvent<TaskMember> item) {
        applicationEventPublisher
                .publishEvent(new TaskMemberAddedEvent(this, item.getItem(), ClusterMode.REMOTE));
    }

    @Override
    public void itemRemoved(ItemEvent<TaskMember> item) {
        applicationEventPublisher
                .publishEvent(new TaskMemberRemovedEvent(this, item.getItem(), ClusterMode.REMOTE));
    }

}
