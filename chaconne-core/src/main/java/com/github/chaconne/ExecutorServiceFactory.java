package com.github.chaconne;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import com.github.cronsmith.scheduler.ExecutorUtils;

/**
 * 
 * @Description: ExecutorServiceFactory
 * @Author: Fred Feng
 * @Date: 06/04/2025
 * @Version 1.0.0
 */
public interface ExecutorServiceFactory {

    ScheduledExecutorService getSchedulerThreads();

    ExecutorService getWorkerThreads();

    default boolean isAutoClosed() {
        return false;
    }

    default void shutdown(ScheduledExecutorService executorService) {
        ExecutorUtils.gracefulShutdown(executorService, 60000L);
    }

    default void shutdown(ExecutorService executorService) {
        ExecutorUtils.gracefulShutdown(executorService, 60000L);
    }

}
