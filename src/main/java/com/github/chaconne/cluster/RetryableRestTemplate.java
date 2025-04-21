package com.github.chaconne.cluster;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @Description: RetryableRestTemplate
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class RetryableRestTemplate extends RestTemplate implements RetryListener, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(RetryableRestTemplate.class);

    public RetryableRestTemplate() {
        super();
    }

    public RetryableRestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    private RetryTemplate retryTemplate;
    private int maxAttempts = 3;
    private Map<Class<? extends Throwable>, Boolean> retryableExceptions;

    public void setRetryTemplate(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setRetryableExceptions(
            Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
        this.retryableExceptions = retryableExceptions;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (retryTemplate == null) {
            retryTemplate = defaultRetryTemplate();
        }
    }

    protected RetryTemplate defaultRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        if (retryableExceptions == null) {
            retryableExceptions = new HashMap<>();
            retryableExceptions.put(RestClientException.class, true);
            retryableExceptions.put(IOException.class, true);
        }
        RetryPolicy retryPolicy =
                maxAttempts > 0 ? new SimpleRetryPolicy(maxAttempts, retryableExceptions)
                        : new NeverRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(new FixedBackOffPolicy());
        retryTemplate.setListeners(new RetryListener[] {this});
        return retryTemplate;
    }

    @Override
    protected <T> T doExecute(URI uri, HttpMethod method, RequestCallback requestCallback,
            ResponseExtractor<T> responseExtractor) throws RestClientException {
        return retryTemplate.execute(context -> {
            return RetryableRestTemplate.super.doExecute(retrieveOriginalUri(uri), null, method,
                    requestCallback, responseExtractor);
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e
                    : new ExhaustedRetryException(e.getMessage(), e);
        });
    }

    protected URI retrieveOriginalUri(URI uri) {
        return uri;
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context,
            RetryCallback<T, E> callback) {
        if (log.isInfoEnabled()) {
            log.info("Start to retry. Retry count: {}", context.getRetryCount());
        }
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
            Throwable e) {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error("Complete to retry. Retry count: {}", context.getRetryCount(), e);
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Complete to retry. Retry count: {}", context.getRetryCount());
            }
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
            Throwable e) {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        RetryableRestTemplate retryableRestTemplate = new RetryableRestTemplate();
        ResponseEntity<String> body = retryableRestTemplate.exchange(
                "http://127.0.0.1:8089/json/?fields=61439", HttpMethod.GET, null, String.class);
        System.out.println(body.getStatusCode());
        System.out.println(body.getBody());
    }

}
