package com.github.chaconne.cluster;

import java.net.URI;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.github.chaconne.client.ApiResponse;
import com.github.chaconne.client.RetryableRestTemplate;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: ApiPing
 * @Author: Fred Feng
 * @Date: 22/04/2025
 * @Version 1.0.0
 */
public class ApiPing implements Ping<TaskMember> {

    private final RetryableRestTemplate restTemplate = new RetryableRestTemplate();

    @Override
    public boolean isAlive(TaskMember tm) throws Exception {
        ResponseEntity<ApiResponse<String>> responseEntity = restTemplate.exchange(
                URI.create(String.format("%s%s", tm.getUrl(), tm.getPingUrl())), HttpMethod.GET,
                null, new ParameterizedTypeReference<ApiResponse<String>>() {});
        return responseEntity.getStatusCode().is2xxSuccessful()
                && "pong".equalsIgnoreCase(responseEntity.getBody().getData());
    }

}
