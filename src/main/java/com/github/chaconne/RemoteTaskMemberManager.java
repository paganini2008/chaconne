package com.github.chaconne;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.collection.IList;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: RemoteTaskMemberManager
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskMemberManager implements MembershipListener, TaskMemberManager,
        InitializingBean, ApplicationEventPublisherAware {

    private final HazelcastInstance hazelcastInstance;

    public RemoteTaskMemberManager(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    private ApplicationEventPublisher applicationEventPublisher;

    private List<TaskMember> schedulers = new CopyOnWriteArrayList<>();
    private IList<TaskMember> executors;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executors = hazelcastInstance.getList("REMOTE_TASK_EXECUTOR");
        hazelcastInstance.getCluster().addMembershipListener(this);
    }

    @Override
    public void addTaskExecutor(TaskMember taskMember) {
        if (taskMember != null && !executors.contains(taskMember)) {
            executors.add(taskMember);
        }
    }

    @Override
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
        ClusterInfo clusterInfo = new ClusterInfo(attributes);
        TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
        taskMemberInstance.setMemberId(clusterInfo.getMemberId());
        taskMemberInstance.setGroup(clusterInfo.getGroup());
        taskMemberInstance.setHost(clusterInfo.getHost());
        taskMemberInstance.setPort(clusterInfo.getPort());
        taskMemberInstance.setContextPath(clusterInfo.getContextPath());
        taskMemberInstance.setMetadata(clusterInfo.getMetadata());
        if (!schedulers.contains(taskMemberInstance)) {
            schedulers.add(taskMemberInstance);
        }
        applicationEventPublisher.publishEvent(new TaskMemberAddedEvent(this, taskMemberInstance));
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        Map<String, String> attributes = membershipEvent.getMember().getAttributes();
        ClusterInfo clusterInfo = new ClusterInfo(attributes);
        TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
        taskMemberInstance.setMemberId(clusterInfo.getMemberId());
        taskMemberInstance.setGroup(clusterInfo.getGroup());
        taskMemberInstance.setHost(clusterInfo.getHost());
        taskMemberInstance.setPort(clusterInfo.getPort());
        taskMemberInstance.setContextPath(clusterInfo.getContextPath());
        taskMemberInstance.setMetadata(clusterInfo.getMetadata());
        if (schedulers.remove(taskMemberInstance)) {
            applicationEventPublisher
                    .publishEvent(new TaskMemberRemovedEvent(this, taskMemberInstance));
        }
    }

}
