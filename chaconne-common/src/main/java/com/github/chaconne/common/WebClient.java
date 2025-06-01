package com.github.chaconne.common;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @Description: WebClient
 * @Author: Fred Feng
 * @Date: 25/05/2025
 * @Version 1.0.0
 */
public class WebClient extends RestTemplate {

    private final SyncRetryer syncRetryer;

    public WebClient(SyncRetryer syncRetryer) {
        this.syncRetryer = syncRetryer;
    }

    public WebClient(SyncRetryer syncRetryer, ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
        this.syncRetryer = syncRetryer;
    }

    public <T> CompletableFuture<ResponseEntity<T>> getForEntity(URI uri, Class<T> requiredType,
            int maxAttempts, Duration interval, int maxAttemptsInBackground,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException {
        return syncRetryer.runAndRetry(() -> {
            return getForEntity(uri, requiredType);
        }, maxAttempts, interval, maxAttemptsInBackground, excludedExceptions);
    }

    public <T> CompletableFuture<ResponseEntity<T>> getForEntity(String uri, Class<T> requiredType,
            Map<String, ?> variables, int maxAttempts, Duration interval,
            int maxAttemptsInBackground, Class<? extends Throwable>[] excludedExceptions)
            throws ExecutionException {
        return syncRetryer.runAndRetry(() -> {
            return getForEntity(uri, requiredType, variables);
        }, maxAttempts, interval, maxAttemptsInBackground, excludedExceptions);
    }

    public <T> CompletableFuture<T> getForObject(URI uri, Class<T> requiredType, int maxAttempts,
            Duration interval, int maxAttemptsInBackground,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException {
        return syncRetryer.runAndRetry(() -> {
            return getForObject(uri, requiredType);
        }, maxAttempts, interval, maxAttemptsInBackground, excludedExceptions);
    }

    public <T> CompletableFuture<T> getForObject(String uri, Class<T> requiredType,
            Map<String, ?> variables, int maxAttempts, Duration interval,
            int maxAttemptsInBackground, Class<? extends Throwable>[] excludedExceptions)
            throws ExecutionException {
        return syncRetryer.runAndRetry(() -> {
            return getForObject(uri, requiredType, variables);
        }, maxAttempts, interval, maxAttemptsInBackground, excludedExceptions);
    }

    public <T> CompletableFuture<ResponseEntity<T>> exchange(URI uri, HttpMethod httpMethod,
            HttpEntity<?> httpEntity, ParameterizedTypeReference<T> responseType, int maxAttempts,
            Duration interval) throws ExecutionException {
        return exchange(uri, httpMethod, httpEntity, responseType, maxAttempts, interval, -1, null);
    }

    public <T> CompletableFuture<ResponseEntity<T>> exchange(URI uri, HttpMethod httpMethod,
            HttpEntity<?> httpEntity, ParameterizedTypeReference<T> responseType, int maxAttempts,
            Duration interval, int maxAttemptsInBackground,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException {
        return syncRetryer.runAndRetry(() -> {
            return exchange(uri, httpMethod, httpEntity, responseType);
        }, maxAttempts, interval, maxAttemptsInBackground, excludedExceptions);
    }

    public <T> CompletableFuture<ResponseEntity<T>> exchange(String url, HttpMethod httpMethod,
            HttpEntity<?> httpEntity, ParameterizedTypeReference<T> responseType,
            Map<String, Object> variables, int maxAttempts, Duration interval)
            throws ExecutionException {
        return exchange(url, httpMethod, httpEntity, responseType, variables, maxAttempts, interval,
                -1, null);
    }

    public <T> CompletableFuture<ResponseEntity<T>> exchange(String url, HttpMethod httpMethod,
            HttpEntity<?> httpEntity, ParameterizedTypeReference<T> responseType,
            Map<String, Object> variables, int maxAttempts, Duration interval,
            int maxAttemptsInBackground, Class<? extends Throwable>[] excludedExceptions)
            throws ExecutionException {
        return syncRetryer.runAndRetry(() -> {
            return exchange(url, httpMethod, httpEntity, responseType, variables);
        }, maxAttempts, interval, maxAttemptsInBackground, excludedExceptions);
    }

}
