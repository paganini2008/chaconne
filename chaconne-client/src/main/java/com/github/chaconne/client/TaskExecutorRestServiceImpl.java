package com.github.chaconne.client;

import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.github.chaconne.common.ApiResponse;
import com.github.chaconne.common.CreateTaskRequest;
import com.github.chaconne.common.TaskIdRequest;
import com.github.chaconne.common.TaskMemberRequest;

/**
 * 
 * @Description: TaskExecutorRestServiceImpl
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class TaskExecutorRestServiceImpl implements TaskExecutorRestService {

    public TaskExecutorRestServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private final RestTemplate restTemplate;
    private HttpHeaders defaultHttpHeaders;

    public void setDefaultHttpHeaders(HttpHeaders defaultHttpHeaders) {
        this.defaultHttpHeaders = defaultHttpHeaders;
    }

    private String defaultServiceId = "chaconne-admin-service";

    public String getDefaultServiceId() {
        return defaultServiceId;
    }

    public void setDefaultServiceId(String defaultServiceId) {
        this.defaultServiceId = defaultServiceId;
    }

    protected HttpHeaders getDefaultHttpHeaders() {
        HttpHeaders httpHeaders = defaultHttpHeaders != null ? new HttpHeaders(defaultHttpHeaders)
                : new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    @Override
    public ResponseEntity<ApiResponse<Boolean>> hasTask(TaskIdRequest taskIdRequest) {
        HttpEntity<Object> httpEntity =
                new HttpEntity<Object>(taskIdRequest, getDefaultHttpHeaders());
        return restTemplate.exchange(URI.create(String.format("lb://%s/chac/exist-task",
                StringUtils.isNotBlank(taskIdRequest.getServiceId()) ? taskIdRequest.getServiceId()
                        : defaultServiceId)),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Boolean>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Boolean>> saveTask(CreateTaskRequest createTaskRequest) {
        HttpEntity<Object> httpEntity =
                new HttpEntity<Object>(createTaskRequest, getDefaultHttpHeaders());
        return restTemplate.exchange(
                URI.create(String.format("lb://%s/chac/save-task",
                        StringUtils.isNotBlank(createTaskRequest.getServiceId())
                                ? createTaskRequest.getServiceId()
                                : defaultServiceId)),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Boolean>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> scheduleTask(TaskIdRequest taskIdRequest) {
        HttpEntity<Object> httpEntity =
                new HttpEntity<Object>(taskIdRequest, getDefaultHttpHeaders());
        return restTemplate.exchange(URI.create(String.format("lb://%s/chac/schedule-task",
                StringUtils.isNotBlank(taskIdRequest.getServiceId()) ? taskIdRequest.getServiceId()
                        : defaultServiceId)),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> scheduleTasks(String serviceId) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(null, getDefaultHttpHeaders());
        return restTemplate.exchange(
                URI.create(String.format("lb://%s/chac/schedule-tasks",
                        StringUtils.isNotBlank(serviceId) ? serviceId : defaultServiceId)),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> removeTask(TaskIdRequest taskIdRequest) {
        HttpEntity<Object> httpEntity =
                new HttpEntity<Object>(taskIdRequest, getDefaultHttpHeaders());
        return restTemplate.exchange(URI.create(String.format("lb://%s/chac/remove-task",
                StringUtils.isNotBlank(taskIdRequest.getServiceId()) ? taskIdRequest.getServiceId()
                        : defaultServiceId)),
                HttpMethod.DELETE, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> registerTaskMember(
            TaskMemberRequest taskMemberRequest) {
        HttpEntity<Object> httpEntity =
                new HttpEntity<Object>(taskMemberRequest, getDefaultHttpHeaders());
        return restTemplate.exchange(
                URI.create(String.format("lb://%s/chac/register",
                        StringUtils.isNotBlank(taskMemberRequest.getServiceId())
                                ? taskMemberRequest.getServiceId()
                                : defaultServiceId)),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

}
