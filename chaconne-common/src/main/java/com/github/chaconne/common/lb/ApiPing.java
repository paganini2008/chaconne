package com.github.chaconne.common.lb;

import java.net.URI;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.github.chaconne.common.ApiResponse;
import com.github.chaconne.common.RetryableRestTemplate;

/**
 * 
 * @Description: ApiPing
 * @Author: Fred Feng
 * @Date: 22/04/2025
 * @Version 1.0.0
 */
public class ApiPing<T extends Candidate> implements Ping<T> {

    private final RetryableRestTemplate restTemplate;

    public ApiPing() {
        restTemplate = new RetryableRestTemplate();
        restTemplate.afterPropertiesSet();
    }

    @Override
    public boolean isAlive(T candidate) throws Exception {
        ResponseEntity<ApiResponse<String>> responseEntity = restTemplate.exchange(
                URI.create(candidate.getServerAddress() + candidate.getPingUrl()), HttpMethod.GET,
                null, new ParameterizedTypeReference<ApiResponse<String>>() {});
        return responseEntity.getStatusCode().is2xxSuccessful()
                && "pong".equalsIgnoreCase(responseEntity.getBody().getData());
    }

}
