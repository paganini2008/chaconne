package com.github.chaconne.common;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 
 * @Description: ISyncRetryer
 * @Author: Fred Feng
 * @Date: 24/05/2025
 * @Version 1.0.0
 */
public interface ISyncRetryer extends IRetryer {

    default CompletableFuture<?> runAndRetry(Runnable task, int maxAttempts, Duration interval,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException {
        return runAndRetry(task, maxAttempts, interval, 0, excludedExceptions);
    }

    default <T> CompletableFuture<T> runAndRetry(Callable<T> task, int maxAttempts,
            Duration interval, Class<? extends Throwable>[] excludedExceptions)
            throws ExecutionException {
        return runAndRetry(task, maxAttempts, interval, 0, excludedExceptions);
    }

    default CompletableFuture<?> runAndRetry(Runnable task, int maxAttempts, Duration interval,
            int maxAttemptsInBackground, Class<? extends Throwable>[] excludedExceptions)
            throws ExecutionException {
        return runAndRetry(() -> {
            task.run();
            return null;
        }, maxAttempts, interval, maxAttemptsInBackground, excludedExceptions);
    }

    <T> CompletableFuture<T> runAndRetry(Callable<T> task, int maxAttempts, Duration interval,
            int maxAttemptsInBackground, Class<? extends Throwable>[] excludedExceptions)
            throws ExecutionException;

}
