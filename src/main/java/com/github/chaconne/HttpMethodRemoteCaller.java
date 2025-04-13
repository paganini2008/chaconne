package com.github.chaconne;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import com.github.chaconne.client.ApiResponse;
import com.github.chaconne.client.RunTaskRequest;

/**
 * 
 * @Description: HttpMethodRemoteCaller
 * @Author: Fred Feng
 * @Date: 16/04/2025
 * @Version 1.0.0
 */
public class HttpMethodRemoteCaller implements TaskMethodRemoteCaller {

    private static final Logger log = LoggerFactory.getLogger(HttpMethodRemoteCaller.class);
    private final TaskSchedulerRestService restTemplate;

    public HttpMethodRemoteCaller(TaskSchedulerRestService restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void call(RunTaskRequest runTaskRequest) {
        ResponseEntity<ApiResponse<Object>> responseEntity = restTemplate.runTask(runTaskRequest);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Result: " + responseEntity.getBody().getData());
        } else if (responseEntity.getStatusCode().isError()) {
            log.error(Arrays.toString(responseEntity.getBody().getErrorDetails()));
        }
    }

}
