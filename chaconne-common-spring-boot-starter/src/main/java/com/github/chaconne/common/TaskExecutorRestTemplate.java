package com.github.chaconne.common;

import java.net.URI;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * 
 * @Description: TaskExecutorRestTemplate
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class TaskExecutorRestTemplate extends RetryableRestTemplate
        implements TaskExecutorRestService {

    public TaskExecutorRestTemplate(URI uri) {
        super();
        this.uri = uri;
    }

    public TaskExecutorRestTemplate(URI uri, ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
        this.uri = uri;
    }

    private final URI uri;
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
    public ResponseEntity<ApiResponse<Boolean>> hasTask(TaskIdRequest taskIdRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(taskIdRequest, getHttpHeaders());
        return this.exchange(URI.create(String.format("%s/chac/exist-task", uri.toString())),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Boolean>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Boolean>> saveTask(CreateTaskRequest createTaskRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(createTaskRequest, getHttpHeaders());
        return this.exchange(URI.create(String.format("%s/chac/save-task", uri.toString())),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Boolean>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> scheduleTask(TaskIdRequest taskIdRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(taskIdRequest, getHttpHeaders());
        return this.exchange(URI.create(String.format("%s/chac/schedule-task", uri.toString())),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> scheduleTasks() {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(null, getHttpHeaders());
        return this.exchange(URI.create(String.format("%s/chac/schedule-tasks", uri.toString())),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> removeTask(TaskIdRequest taskIdRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(taskIdRequest, getHttpHeaders());
        return this.exchange(URI.create(String.format("%s/chac/remove-task", uri.toString())),
                HttpMethod.DELETE, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> registerTaskMember(
            TaskMemberRequest taskMemberRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(taskMemberRequest, getHttpHeaders());
        return this.exchange(URI.create(String.format("%s/chac/register", uri.toString())),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

}
