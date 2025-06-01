package com.github.chaconne.cluster;

import java.util.Set;
import org.apache.commons.collections4.IteratorUtils;
import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: TaskMemberLock
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
public final class TaskMemberLock {

    private final HazelcastInstance hazelcastInstance;

    public TaskMemberLock(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public boolean tryLock() {
        Member localMember = hazelcastInstance.getCluster().getLocalMember();
        Set<Member> members = hazelcastInstance.getCluster().getMembers();
        Member firstMember = IteratorUtils.first(members.iterator());
        return firstMember != null && firstMember.equals(localMember);
    }
}
