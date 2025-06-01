package com.github.chaconne.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import com.github.chaconne.common.utils.ExceptionUtils;
import com.github.chaconne.common.utils.ExecutorUtils;

/**
 * 
 * @Description: AsyncRetryer
 * @Author: Fred Feng
 * @Date: 24/05/2025
 * @Version 1.0.0
 */
public class AsyncRetryer implements Runnable, IRetryer {

    private final Map<Callable<?>, AsyncRetryContext> queue = new ConcurrentHashMap<>();
    private ScheduledFuture<?> scheduledFuture;
    private ScheduledExecutorService executorService;
    private boolean autoClosed;
    private List<AsyncRetryListener> asyncRetryListeners =
            Collections.singletonList(new AsyncRetryLogger());
    private int checkInterval = 5;

    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }

    public void setAsyncRetryListeners(List<AsyncRetryListener> asyncRetryListeners) {
        this.asyncRetryListeners = asyncRetryListeners;
    }

    public void setExecutor(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void start() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            autoClosed = true;
        }
    }

    @Override
    public void close() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        if (executorService != null && autoClosed) {
            ExecutorUtils.gracefulShutdown(executorService, 60000L);
        }
    }

    public Map<Callable<?>, AsyncRetryContext> getQueue() {
        return queue;
    }

    @Override
    public <T> CompletableFuture<T> runAndRetry(Callable<T> task) throws ExecutionException {
        return runAndRetry(task, 0);
    }

    @Override
    public <T> CompletableFuture<T> runAndRetry(Callable<T> task, int maxAttempts)
            throws ExecutionException {
        return runAndRetry(task, maxAttempts, null);
    }

    @Override
    public <T> CompletableFuture<T> runAndRetry(Callable<T> task, int maxAttempts,
            Class<? extends Throwable>[] excludedExceptions) throws ExecutionException {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        try {
            completableFuture.complete(runTask(task));
        } catch (Throwable e) {
            retry(task, maxAttempts, excludedExceptions, e, completableFuture);
        }
        return completableFuture;
    }

    protected <T> T runTask(Callable<T> task) throws Exception {
        return task.call();
    }

    protected final <T> void retry(Callable<T> task, int maxAttempts,
            Class<? extends Throwable>[] excludedExceptions, Throwable cause,
            CompletableFuture<T> completableFuture) throws ExecutionException {
        if (maxAttempts < 0) {
            throw new ExecutionException(cause);
        }
        if (maxAttempts == 0) {
            completableFuture.complete(null);
            return;
        }
        Throwable actualThrown = translateException(cause);
        if (ExceptionUtils.ignoreException(actualThrown, excludedExceptions)) {
            completableFuture.complete(null);
            return;
        }
        queue.put(task, new AsyncRetryContext(maxAttempts, excludedExceptions, actualThrown,
                completableFuture));
        synchronized (this) {
            if (scheduledFuture == null) {
                scheduledFuture = executorService.scheduleWithFixedDelay(this, checkInterval,
                        checkInterval, TimeUnit.SECONDS);
            }
        }
    }

    protected Throwable translateException(Throwable cause) {
        return ExceptionUtils.getOriginalException(cause);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        List<Callable<?>> copy = new ArrayList<Callable<?>>(queue.keySet());
        for (Callable<?> task : copy) {
            long time = System.currentTimeMillis();
            AsyncRetryContext retryContext = queue.get(task);
            retryContext.setRetryCount(retryContext.getRetryCount() + 1);
            boolean finished = false;
            asyncRetryListeners.forEach(l -> {
                l.begin(retryContext);
            });
            Object returnValue = null;
            try {
                Object result = task.call();
                finished = true;
                retryContext.setCause(null);
                asyncRetryListeners.forEach(l -> {
                    l.onSuccess(retryContext, result);
                });
                returnValue = result;
            } catch (Throwable e) {
                Throwable actualThrown = translateException(e);
                if (ExceptionUtils.ignoreException(actualThrown,
                        retryContext.getExcludedExceptions())) {
                    finished = true;
                } else if (retryContext.getMaxAttempts() > 0) {
                    if (retryContext.getRetryCount() == retryContext.getMaxAttempts()) {
                        finished = true;
                    }
                }
                retryContext.setCause(actualThrown);
                asyncRetryListeners.forEach(l -> {
                    l.onError(retryContext);
                });
            } finally {
                retryContext.setTotalTime(System.currentTimeMillis() - time);
                if (finished) {
                    queue.remove(task);
                    ((CompletableFuture<Object>) retryContext.getFuture()).complete(returnValue);
                }
                asyncRetryListeners.forEach(l -> {
                    l.end(retryContext);
                });
            }
        }
        synchronized (this) {
            if (queue.isEmpty() && scheduledFuture != null && !scheduledFuture.isDone()) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            }
        }
    }

    public static class AsyncRetryContext {

        private long totalTime;
        private int maxAttempts;
        private int retryCount;
        private Class<? extends Throwable>[] excludedExceptions;
        private Throwable cause;
        private CompletableFuture<?> future;

        AsyncRetryContext() {

        }

        AsyncRetryContext(int maxAttempts, Class<? extends Throwable>[] excludedExceptions,
                Throwable cause, CompletableFuture<?> future) {
            this.maxAttempts = maxAttempts;
            this.excludedExceptions = excludedExceptions;
            this.cause = cause;
            this.future = future;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public Class<? extends Throwable>[] getExcludedExceptions() {
            return excludedExceptions;
        }

        public void setExcludedExceptions(Class<? extends Throwable>[] excludedExceptions) {
            this.excludedExceptions = excludedExceptions;
        }

        public Throwable getCause() {
            return cause;
        }

        public void setCause(Throwable cause) {
            this.cause = cause;
        }

        public long getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(long totalTime) {
            this.totalTime += totalTime;
        }

        CompletableFuture<?> getFuture() {
            return future;
        }

        void setFuture(CompletableFuture<Object> future) {
            this.future = future;
        }

        @Override
        public String toString() {
            return String.format("Retry Process: %s/%s, Total Take: %s (ms), Reason: %s",
                    retryCount, maxAttempts, totalTime,
                    cause != null ? cause.getMessage() : "<NONE>");
        }
    }

}
