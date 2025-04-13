package com.github.chaconne;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;

/**
 * 
 * @Description: LocalTaskMemberManager
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public class LocalTaskMemberManager
        implements MembershipListener, TaskMemberManager, ApplicationEventPublisherAware {

    private List<TaskMember> schedulers = new CopyOnWriteArrayList<>();
    private List<TaskMember> executors = new CopyOnWriteArrayList<>();

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void addTaskExecutor(TaskMember taskMember) {
        if (taskMember != null && !executors.contains(taskMember)) {
            executors.add(taskMember);
        }
    }

    public int positionOfScheduler(TaskMember taskMember) {
        if (schedulers.isEmpty()) {
            return -1;
        }
        return schedulers.indexOf(taskMember);
    }

    @Override
    public int positionOfExecutor(TaskMember taskMember) {
        if (executors.isEmpty()) {
            return -1;
        }
        return executors.indexOf(taskMember);
    }

    @Override
    public int countOfScheduler() {
        return schedulers.size();
    }

    @Override
    public int countOfExecutor() {
        return executors.size();
    }

    @Override
    public List<TaskMember> findSchedulers(String group) {
        return schedulers.stream().filter(tm -> tm.getGroup().equals(group)).toList();
    }

    @Override
    public List<TaskMember> findExecutors(String group) {
        return executors.stream().filter(tm -> tm.getGroup().equals(group)).toList();
    }

    @Override
    public List<String> getExecutorGroups() {
        return executors.stream().map(e -> e.getGroup()).toList();
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        Map<String, String> attributes = membershipEvent.getMember().getAttributes();
        TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
        taskMemberInstance.setMemberId(attributes.get("memberId"));
        taskMemberInstance.setGroup(attributes.get("group"));
        taskMemberInstance.setHost(attributes.get("host"));
        taskMemberInstance.setPort(Integer.parseInt(attributes.get("port")));
        taskMemberInstance.setContextPath(attributes.get("contextPath"));
        if (!schedulers.contains(taskMemberInstance)) {
            schedulers.add(taskMemberInstance);
        }
        if (!executors.contains(taskMemberInstance)) {
            executors.add(taskMemberInstance);
        }
        applicationEventPublisher.publishEvent(new TaskMemberAddedEvent(this, taskMemberInstance));
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        Map<String, String> attributes = membershipEvent.getMember().getAttributes();
        TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
        taskMemberInstance.setMemberId(attributes.get("memberId"));
        taskMemberInstance.setGroup(attributes.get("group"));
        taskMemberInstance.setHost(attributes.get("host"));
        taskMemberInstance.setPort(Integer.parseInt(attributes.get("port")));
        taskMemberInstance.setContextPath(attributes.get("contextPath"));
        if (schedulers.remove(taskMemberInstance) && executors.remove(taskMemberInstance)) {
            applicationEventPublisher
                    .publishEvent(new TaskMemberRemovedEvent(this, taskMemberInstance));
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
