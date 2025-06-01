package com.github.chaconne.cluster;

import java.net.URI;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.github.chaconne.common.ApiResponse;
import com.github.chaconne.common.TaskIdRequest;

/**
 * 
 * @Description: TaskSchedulerRestServiceImpl
 * @Author: Fred Feng
 * @Date: 16/04/2025
 * @Version 1.0.0
 */
public class TaskSchedulerRestServiceImpl implements TaskSchedulerRestService {

    private final RestTemplate restTemplate;

    public TaskSchedulerRestServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders defaultHttpHeaders;

    public void setDefaultHttpHeaders(HttpHeaders defaultHttpHeaders) {
        this.defaultHttpHeaders = defaultHttpHeaders;
    }

    protected HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = defaultHttpHeaders != null ? new HttpHeaders(defaultHttpHeaders)
                : new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> runTask(TaskIdRequest taskIdRequest) {
        String hostUrl = taskIdRequest.getUrl();
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(taskIdRequest, getHttpHeaders());
        return restTemplate.exchange(URI.create(String.format("%s/chac/run-task", hostUrl)),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

}
