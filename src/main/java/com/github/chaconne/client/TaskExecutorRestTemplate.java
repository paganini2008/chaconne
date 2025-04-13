package com.github.chaconne.client;

import java.net.URI;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import com.github.chaconne.LoadBalancedManager;
import com.github.chaconne.RetryableRestTemplate;

/**
 * 
 * @Description: TaskExecutorRestTemplate
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class TaskExecutorRestTemplate extends RetryableRestTemplate
        implements InitializingBean, DisposableBean {

    public TaskExecutorRestTemplate(LoadBalancedManager<URI> loadBalancedManager) {
        super();
        this.loadBalancedManager = loadBalancedManager;
    }

    public TaskExecutorRestTemplate(LoadBalancedManager<URI> loadBalancedManager,
            ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
        this.loadBalancedManager = loadBalancedManager;
    }

    private final LoadBalancedManager<URI> loadBalancedManager;
    private HttpHeaders defaultHttpHeaders;

    public void setDefaultHttpHeaders(HttpHeaders defaultHttpHeaders) {
        this.defaultHttpHeaders = defaultHttpHeaders;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (loadBalancedManager instanceof InitializingBean) {
            ((InitializingBean) loadBalancedManager).afterPropertiesSet();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (loadBalancedManager instanceof DisposableBean) {
            ((DisposableBean) loadBalancedManager).destroy();
        }
    }

    protected HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = defaultHttpHeaders != null ? new HttpHeaders(defaultHttpHeaders)
                : new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    public ResponseEntity<ApiResponse<Boolean>> hasTask(TaskIdRequest taskIdRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(taskIdRequest, getHttpHeaders());
        return this.exchange(URI.create("lb://task-scheduler/chac/exist-task"), HttpMethod.POST,
                httpEntity, new ParameterizedTypeReference<ApiResponse<Boolean>>() {});
    }

    public ResponseEntity<ApiResponse<Object>> saveTask(CreateTaskRequest createTaskRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(createTaskRequest, getHttpHeaders());
        return this.exchange(URI.create("lb://task-scheduler/chac/save-task"), HttpMethod.POST,
                httpEntity, new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    public ResponseEntity<ApiResponse<Object>> removeTask(TaskIdRequest taskIdRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(taskIdRequest, getHttpHeaders());
        return this.exchange(URI.create("lb://task-scheduler/chac/remove-task"), HttpMethod.DELETE,
                httpEntity, new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    public ResponseEntity<ApiResponse<Object>> registerTaskMember(
            TaskMemberRequest taskMemberRequest) {
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(taskMemberRequest, getHttpHeaders());
        return this.exchange(URI.create("lb://task-scheduler/chac/register"), HttpMethod.POST,
                httpEntity, new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

    @Override
    protected URI retrieveOriginalUri(URI uri) {
        return loadBalancedManager.getNextCandidate(uri);
    }

}
