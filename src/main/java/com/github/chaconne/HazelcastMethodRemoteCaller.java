package com.github.chaconne;

import com.github.chaconne.client.RunTaskRequest;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: HazelcastMethodRemoteCaller
 * @Author: Fred Feng
 * @Date: 16/04/2025
 * @Version 1.0.0
 */
public class HazelcastMethodRemoteCaller implements TaskMethodRemoteCaller {

    private final IQueue<RunTaskRequest> queue;

    public HazelcastMethodRemoteCaller(HazelcastInstance hazelcastInstance,
            TaskMemberManager taskMemberManager) {
        this.queue = hazelcastInstance.getQueue("TASK_METHOD_REMOTE_CALLER");
    }

    @Override
    public void call(RunTaskRequest runTaskRequest) {
        queue.add(runTaskRequest);
    }

}
