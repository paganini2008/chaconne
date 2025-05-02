package com.github.chaconne.cluster;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import com.github.chaconne.AbstractCustomTask;
import com.github.chaconne.CustomTask;
import com.github.chaconne.CustomTaskFactory;
import com.github.chaconne.TaskId;
import com.github.chaconne.common.ApiResponse;
import com.github.chaconne.common.RunTaskRequest;

/**
 * 
 * @Description: RemoteCustomTaskFactory
 * @Author: Fred Feng
 * @Date: 26/04/2025
 * @Version 1.0.0
 */
public class RemoteCustomTaskFactory implements CustomTaskFactory {

    private final TaskSchedulerRestService taskSchedulerRestService;

    public RemoteCustomTaskFactory(TaskSchedulerRestService taskSchedulerRestService) {
        this.taskSchedulerRestService = taskSchedulerRestService;
    }

    @Override
    public CustomTask createTaskObject(Map<String, Object> record) {
        return new RemoteCustomTask(record);
    }

    class RemoteCustomTask extends AbstractCustomTask {

        RemoteCustomTask(Map<String, Object> record) {
            super(record);
        }

        @Override
        protected Object invokeTaskMethod(TaskId taskId, String taskClassName,
                String taskMethodName, String initialParameter) {
            RunTaskRequest runTaskRequest = new RunTaskRequest();
            runTaskRequest.setTaskGroup(taskId.getGroup());
            runTaskRequest.setTaskName(taskId.getName());
            runTaskRequest.setTaskClass(taskClassName);
            runTaskRequest.setTaskMethod(taskMethodName);
            runTaskRequest.setUrl(getUrl());
            runTaskRequest.setInitialParameter(initialParameter);
            ResponseEntity<ApiResponse<Object>> responseEntity =
                    taskSchedulerRestService.runTask(runTaskRequest);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody().getData();
            }
            String[] errorDetails = responseEntity.getBody().getErrorDetails();
            throw new RemoteTaskInvocationException(responseEntity.getBody().getMsg(),
                    errorDetails);
        }

        @Override
        public void handleResult(Object result, Throwable reason) {
            if (log.isTraceEnabled()) {
                log.trace("ResponseBodyString: ", result != null ? result.toString() : "<NONE>");
            }
        }

    }


}
