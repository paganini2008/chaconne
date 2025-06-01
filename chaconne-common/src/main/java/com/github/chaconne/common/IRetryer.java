package com.github.chaconne.common;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 
 * @Description: IRetryer
 * @Author: Fred Feng
 * @Date: 24/05/2025
 * @Version 1.0.0
 */
public interface IRetryer {

    void start();

    void close();

    default CompletableFuture<?> runAndRetry(Runnable task) throws ExecutionException {
        return runAndRetry(task, 0);
    }

    <T> CompletableFuture<T> runAndRetry(Callable<T> task) throws ExecutionException;

    default CompletableFuture<?> runAndRetry(Runnable task, int maxAttempts)
            throws ExecutionException {
        return runAndRetry(task, maxAttempts, null);
    }

    <T> CompletableFuture<T> runAndRetry(Callable<T> task, int maxAttempts)
            throws ExecutionException;

    default CompletableFuture<?> runAndRetry(Runnable task, int maxAttempts,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException {
        return runAndRetry(() -> {
            task.run();
            return null;
        }, maxAttempts, excludedExceptions);
    }

    <T> CompletableFuture<T> runAndRetry(Callable<T> task, int maxAttempts,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException;

}
