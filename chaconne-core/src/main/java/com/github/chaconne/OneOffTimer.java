package com.github.chaconne;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: OneOffTimer
 * @Author: Fred Feng
 * @Date: 04/04/2025
 * @Version 1.0.0
 */
public class OneOffTimer implements Runnable {

    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> scheduledFuture;

    public OneOffTimer(int initialDeplay, int interval, TimeUnit timeUnit, OneOffTask timerTask) {
        this.initialDeplay = initialDeplay;
        this.interval = interval;
        this.timeUnit = timeUnit;
        this.timerTask = timerTask;
    }

    public OneOffTimer(ScheduledExecutorService executorService, int initialDeplay, int interval,
            TimeUnit timeUnit, OneOffTask timerTask) {
        this(initialDeplay, interval, timeUnit, timerTask);
        this.executorService = executorService;
    }

    private final int initialDeplay;
    private final int interval;
    private final TimeUnit timeUnit;
    private final OneOffTask timerTask;

    public void start() {
        start(false, true);
    }

    public void start(boolean runFirst, boolean fixedRate) {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        if (runFirst) {
            run();
        }
        if (fixedRate) {
            scheduledFuture =
                    executorService.scheduleAtFixedRate(this, initialDeplay, interval, timeUnit);
        } else {
            scheduledFuture =
                    executorService.scheduleWithFixedDelay(this, initialDeplay, interval, timeUnit);
        }
    }

    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            try {
                executorService.shutdown();
            } catch (Exception e) {
                executorService.shutdownNow();
            }
        }
    }

    @Override
    public final void run() {
        boolean continued = true;
        try {
            continued = timerTask.execute();
        } catch (Throwable e) {
            continued = timerTask.onError(e);
        } finally {
            if (!continued) {
                if (scheduledFuture != null && scheduledFuture.cancel(true)) {
                    timerTask.onCancellation();
                }
                close();
            }
        }
    }

}
