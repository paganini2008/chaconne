package com.github.chaconne;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 
 * @Description: DefaultExecutorServiceFactory
 * @Author: Fred Feng
 * @Date: 06/04/2025
 * @Version 1.0.0
 */
public class DefaultExecutorServiceFactory implements ExecutorServiceFactory {


    public DefaultExecutorServiceFactory() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public DefaultExecutorServiceFactory(int nThreads) {
        this.nThreads = nThreads;
    }

    private final int nThreads;

    @Override
    public ScheduledExecutorService getSchedulerThreads() {
        return Executors.newScheduledThreadPool(nThreads);
    }

    @Override
    public ExecutorService getWorkerThreads() {
        return Executors.newFixedThreadPool(nThreads * 2);
    }

    @Override
    public boolean isAutoClosed() {
        return true;
    }

}
