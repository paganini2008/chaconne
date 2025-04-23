package com.github.chaconne.cluster;

import com.github.chaconne.DefaultTaskInvocation;
import com.github.chaconne.TaskId;
import com.github.chaconne.TaskInvocationException;
import com.github.chaconne.client.RunTaskRequest;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: LocalTaskInvocation
 * @Author: Fred Feng
 * @Date: 22/04/2025
 * @Version 1.0.0
 */
public class LocalTaskInvocation extends DefaultTaskInvocation {

    private final HazelcastInstance hazelcastInstance;

    public LocalTaskInvocation(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public Object invokeTaskMethod(TaskId taskId, String taskClassName, String taskMethodName,
            String initialParameter) {
        RunTaskRequest runTaskRequest = new RunTaskRequest();
        runTaskRequest.setTaskGroup(taskId.getGroup());
        runTaskRequest.setTaskName(taskId.getName());
        runTaskRequest.setInitialParameter(initialParameter);
        runTaskRequest.setTaskClass(taskClassName);
        runTaskRequest.setTaskMethod(taskMethodName);
        IQueue<RunTaskRequest> queue = hazelcastInstance.getQueue(taskId.getGroup());
        try {
            queue.put(runTaskRequest);
        } catch (Exception e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
        return null;
    }

}
