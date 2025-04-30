package com.github.chaconne.cluster;

import java.util.Map;
import com.github.chaconne.AbstractCustomTask;
import com.github.chaconne.CustomTask;
import com.github.chaconne.CustomTaskFactory;
import com.github.chaconne.TaskId;
import com.github.chaconne.TaskInvocationException;
import com.github.chaconne.client.RunTaskRequest;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: LocalCustomTaskFactory
 * @Author: Fred Feng
 * @Date: 25/04/2025
 * @Version 1.0.0
 */
public class LocalCustomTaskFactory implements CustomTaskFactory {

    private final HazelcastInstance hazelcastInstance;

    public LocalCustomTaskFactory(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public CustomTask createTaskObject(Map<String, Object> record) {
        return new LocalCustomTask(record);
    }

    /**
     * 
     * @Description: LocalCustomTask
     * @Author: Fred Feng
     * @Date: 26/04/2025
     * @Version 1.0.0
     */
    private class LocalCustomTask extends AbstractCustomTask {

        LocalCustomTask(Map<String, Object> record) {
            super(record);
        }

        @Override
        protected Object invokeTaskMethod(TaskId taskId, String taskClassName,
                String taskMethodName, String initialParameter) {
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

        @Override
        public void handleResult(Object result, Throwable reason) {}


    }

}
