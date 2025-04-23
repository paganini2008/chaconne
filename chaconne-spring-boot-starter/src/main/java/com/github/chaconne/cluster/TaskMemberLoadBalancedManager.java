package com.github.chaconne.cluster;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

/**
 * 
 * @Description: TaskMemberLoadBalancedManager
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public class TaskMemberLoadBalancedManager extends DefaultLoadBalancedManager<TaskMember>
        implements SmartApplicationListener {

    private TaskMember currentTaskMember;

    public void setCurrentTaskMember(TaskMember currentTaskMember) {
        this.currentTaskMember = currentTaskMember;
    }

    @Override
    protected boolean shouldIgnored(TaskMember taskMember) {
        return currentTaskMember != null && currentTaskMember.equals(taskMember);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof TaskMemberAddedEvent) {
            TaskMemberAddedEvent addedEvent = (TaskMemberAddedEvent) event;
            if (addedEvent.getClusterMode() == ClusterMode.REMOTE) {
                addCandidate(addedEvent.getTaskMember());
            }
        } else if (event instanceof TaskMemberRemovedEvent) {
            TaskMemberRemovedEvent removedEvent = (TaskMemberRemovedEvent) event;
            if (removedEvent.getClusterMode() == ClusterMode.REMOTE) {
                removeCandidate(removedEvent.getTaskMember());
            }
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(TaskMemberAddedEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }

}
