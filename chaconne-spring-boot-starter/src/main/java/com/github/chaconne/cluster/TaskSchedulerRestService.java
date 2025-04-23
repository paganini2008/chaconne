package com.github.chaconne.cluster;

import org.springframework.http.ResponseEntity;
import com.github.chaconne.client.ApiResponse;
import com.github.chaconne.client.TaskIdRequest;

/**
 * 
 * @Description: TaskSchedulerRestService
 * @Author: Fred Feng
 * @Date: 20/04/2025
 * @Version 1.0.0
 */
public interface TaskSchedulerRestService {

    ResponseEntity<ApiResponse<Object>> runTask(TaskIdRequest request);

}
