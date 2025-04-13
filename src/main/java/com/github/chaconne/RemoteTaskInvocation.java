package com.github.chaconne;

import org.springframework.http.ResponseEntity;
import com.github.chaconne.client.ApiResponse;
import com.github.chaconne.client.RunTaskRequest;

/**
 * 
 * @Description: RemoteTaskInvocation
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskInvocation extends DefaultTaskInvocation {

    private final TaskSchedulerRestService taskSchedulerRestService;

    public RemoteTaskInvocation(TaskSchedulerRestService taskSchedulerRestService) {
        this.taskSchedulerRestService = taskSchedulerRestService;
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
        ResponseEntity<ApiResponse<Object>> responseEntity =
                taskSchedulerRestService.runTask(runTaskRequest);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody().getData();
        }
        String[] errorDetails = responseEntity.getBody().getErrorDetails();
        throw new RemoteTaskInvocationException(responseEntity.getBody().getMsg(), errorDetails);
    }



}
