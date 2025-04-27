package com.github.chaconne.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

public class TestMain {

    public static class TestListener implements RetryListener {

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context,
                RetryCallback<T, E> callback) {
            System.out.println("TestMain.TestListener.open()");
            return true;
        }

        @Override
        public <T, E extends Throwable> void close(RetryContext context,
                RetryCallback<T, E> callback, Throwable throwable) {
            System.out.println("TestMain.TestListener.close()");
        }

        @Override
        public <T, E extends Throwable> void onSuccess(RetryContext context,
                RetryCallback<T, E> callback, T result) {
            System.out.println("TestMain.TestListener.onSuccess()");
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context,
                RetryCallback<T, E> callback, Throwable throwable) {
            System.out.println("TestMain.TestListener.onError()");
        }

    }

    private static RetryTemplate getRetryTemplate() {
        int maxAttempts = 3;
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = null;
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
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setListeners(new RetryListener[] {new TestListener()});
        return retryTemplate;
    }

    public static void main(String[] args) {
        int N = 101;
        RetryTemplate retryTemplate = getRetryTemplate();
        boolean result = retryTemplate.execute(context -> {
            if (N % 2 != 0) {
                throw new ResourceAccessException("qqq");
            }
            return true;
        }, context -> {
            Throwable e = context.getLastThrowable();
            throw e instanceof RestClientException ? (RestClientException) e
                    : new ExhaustedRetryException(e.getMessage(), e);
        });
        System.out.println("Answer: " + result);


    }

}
