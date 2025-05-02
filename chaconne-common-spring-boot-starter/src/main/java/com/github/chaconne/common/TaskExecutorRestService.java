package com.github.chaconne.common;

import org.springframework.http.ResponseEntity;

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

    ResponseEntity<ApiResponse<Object>> scheduleTasks();

    ResponseEntity<ApiResponse<Object>> removeTask(TaskIdRequest taskIdRequest);

    ResponseEntity<ApiResponse<Object>> registerTaskMember(TaskMemberRequest taskMemberRequest);

}
