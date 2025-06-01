package com.github.chaconne.common.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: ExecutorUtils
 * @Author: Fred Feng
 * @Date: 06/04/2025
 * @Version 1.0.0
 */
public abstract class ExecutorUtils {

    public static boolean isShutdown(Executor executor) {
        if (executor instanceof ExecutorService) {
            if (((ExecutorService) executor).isShutdown()) {
                return true;
            }
        }
        return false;
    }

    public static void gracefulShutdown(Executor executor, final long timeout) {
        if (!(executor instanceof ExecutorService) || isShutdown(executor)) {
            return;
        }
        final ExecutorService es = (ExecutorService) executor;
        try {
            es.shutdown();
        } catch (RuntimeException ex) {
            return;
        }
        if (!isShutdown(es)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        es.shutdownNow();
                    } catch (RuntimeException ex) {
                        return;
                    }
                    try {
                        es.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }

}
