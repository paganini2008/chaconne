package com.github.chaconne.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: TaskMembershipEventPublisher
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
public class TaskMembershipEventPublisher implements InitializingBean, MembershipListener,
        ApplicationEventPublisherAware, ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(TaskMembershipEventPublisher.class);

    private final HazelcastInstance hazelcastInstance;

    public TaskMembershipEventPublisher(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        hazelcastInstance.getCluster().addMembershipListener(this);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        hazelcastInstance.getCluster().getMembers().forEach(member -> {
            applicationEventPublisher
                    .publishEvent(new TaskMemberAddedEvent(this, getTaskMember(member)));
        });
    }

    protected TaskMember getTaskMember(Member member) {
        return ClusterUtils.getTaskMember(member);
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        TaskMember taskMember = getTaskMember(membershipEvent.getMember());
        log.info("New task member added: " + taskMember);
        applicationEventPublisher.publishEvent(new TaskMemberAddedEvent(this, taskMember));
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        TaskMember taskMember = getTaskMember(membershipEvent.getMember());
        log.info("New task member removed: " + taskMember);
        applicationEventPublisher.publishEvent(new TaskMemberAddedEvent(this, taskMember));
    }
}
