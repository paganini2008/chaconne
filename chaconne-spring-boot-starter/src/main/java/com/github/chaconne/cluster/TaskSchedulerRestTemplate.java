package com.github.chaconne.cluster;

import java.net.URI;
import java.util.Collection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import com.github.chaconne.client.ApiResponse;
import com.github.chaconne.client.RetryableRestTemplate;
import com.github.chaconne.client.TaskIdRequest;

/**
 * 
 * @Description: TaskSchedulerRestTemplate
 * @Author: Fred Feng
 * @Date: 16/04/2025
 * @Version 1.0.0
 */
public class TaskSchedulerRestTemplate extends RetryableRestTemplate
        implements TaskSchedulerRestService {

    public TaskSchedulerRestTemplate(ClientHttpRequestFactory requestFactory,
            Collection<ClientHttpRequestInterceptor> interceptors) {
        super(requestFactory);
        if (interceptors != null && interceptors.size() > 0) {
            getInterceptors().addAll(interceptors);
        }
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
        return this.exchange(URI.create(String.format("%s/chac/run-task", hostUrl)),
                HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<ApiResponse<Object>>() {});
    }

}
