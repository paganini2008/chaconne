package com.github.chaconne.client;

import org.springframework.http.ResponseEntity;
import com.github.chaconne.common.ApiResponse;
import com.github.chaconne.common.CreateTaskRequest;
import com.github.chaconne.common.TaskIdRequest;
import com.github.chaconne.common.TaskMemberRequest;

/**
 * 
 * @Description: TaskExecutorRestService
 * @Author: Fred Feng
 * @Date: 27/04/2025
 * @Version 1.0.0
 */
public interface TaskExecutorRestService {

    ResponseEntity<ApiResponse<Boolean>> hasTask(TaskIdRequest taskIdRequest);

    ResponseEntity<ApiResponse<Boolean>> saveTask(CreateTaskRequest createTaskRequest);

    ResponseEntity<ApiResponse<Object>> scheduleTask(TaskIdRequest taskIdRequest);

    ResponseEntity<ApiResponse<Object>> scheduleTasks(String serviceId);

    ResponseEntity<ApiResponse<Object>> removeTask(TaskIdRequest taskIdRequest);

    ResponseEntity<ApiResponse<Object>> registerTaskMember(TaskMemberRequest taskMemberRequest);

}
