package com.github.chaconne.cluster;

import java.util.Set;
import com.github.cronsmith.IteratorUtils;
import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: TaskMemberLock
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
public class TaskMemberLock {

    private final HazelcastInstance hazelcastInstance;

    public TaskMemberLock(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public boolean tryLock() {
        Member localMember = hazelcastInstance.getCluster().getLocalMember();
        Set<Member> members = hazelcastInstance.getCluster().getMembers();
        Member firstMember = IteratorUtils.getFirst(members, null);
        return firstMember != null && firstMember.equals(localMember);
    }
}
