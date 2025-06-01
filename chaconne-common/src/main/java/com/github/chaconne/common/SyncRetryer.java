package com.github.chaconne.common;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.RetryException;
import org.springframework.retry.RetryListener;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * 
 * @Description: SyncRetryer
 * @Author: Fred Feng
 * @Date: 24/05/2025
 * @Version 1.0.0
 */
public class SyncRetryer extends AsyncRetryer implements ISyncRetryer {

    private final Map<Callable<?>, RetryTemplate> templates = new ConcurrentHashMap<>();

    public void setRetryListeners(List<RetryListener> retryListeners) {
        this.retryListeners = retryListeners;
    }

    private List<RetryListener> retryListeners = Collections.singletonList(new RetryLogger());

    @Override
    public <T> CompletableFuture<T> runAndRetry(Callable<T> task, int maxAttempts,
            Duration interval, int maxAttemptsInBackground,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException {
        return super.runAndRetry(() -> {
            RetryTemplate retryTemplate = templates.computeIfAbsent(task, key -> {
                return RetryTemplate.builder()
                        .customPolicy(maxAttempts > 0 ? new MaxAttemptsRetryPolicy(maxAttempts)
                                : new NeverRetryPolicy())
                        .fixedBackoff(interval)
                        .notRetryOn(excludedExceptions != null ? List.of(excludedExceptions)
                                : Collections.emptyList())
                        .withListeners(retryListeners).build();
            });
            return retryTemplate.execute(ctx -> {
                return task.call();
            }, ctx -> {
                templates.remove(task);
                Throwable e = ctx.getLastThrowable();
                throw new ExhaustedRetryException(e.getMessage(), e);
            });

        }, maxAttemptsInBackground, excludedExceptions);
    }

    @Override
    public <T> CompletableFuture<T> runAndRetry(Callable<T> task, int maxAttempts,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException {
        return super.runAndRetry(() -> {
            RetryTemplate retryTemplate = templates.computeIfAbsent(task, key -> {
                return RetryTemplate.builder()
                        .customPolicy(maxAttempts > 0 ? new MaxAttemptsRetryPolicy(maxAttempts)
                                : new NeverRetryPolicy())
                        .noBackoff()
                        .notRetryOn(excludedExceptions != null ? List.of(excludedExceptions)
                                : Collections.emptyList())
                        .withListeners(retryListeners).build();
            });
            return retryTemplate.execute(ctx -> {
                return task.call();
            }, ctx -> {
                Throwable e = ctx.getLastThrowable();
                throw new ExhaustedRetryException(e.getMessage(), e);
            });

        }, 0, excludedExceptions);
    }

    @Override
    protected Throwable translateException(Throwable e) {
        Throwable thrown = super.translateException(e);
        if (thrown instanceof RetryException) {
            return ((RetryException) thrown).getCause();
        }
        return thrown;
    }

}
