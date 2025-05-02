package com.github.chaconne.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.github.chaconne.common.TaskAnnotationBeanFinder;
import com.github.cronsmith.scheduler.ExecutorUtils;

/**
 * 
 * @Description: FinalRetryer
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
public class FinalRetryer implements Runnable, InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(TaskAnnotationBeanFinder.class);

    private final List<Runnable> queue = new CopyOnWriteArrayList<>();

    private ScheduledFuture<?> scheduledFuture;
    private ScheduledExecutorService executorService;

    public void setExecutor(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        if (executorService != null) {
            ExecutorUtils.gracefulShutdown(executorService, 60000L);
        }
    }

    public void retry(Runnable r) {
        queue.add(r);
        synchronized (this) {
            if (scheduledFuture == null) {
                scheduledFuture =
                        executorService.scheduleWithFixedDelay(this, 5, 5, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void run() {
        List<Runnable> copy = new ArrayList<Runnable>(queue);
        for (Runnable r : copy) {
            try {
                r.run();
                queue.remove(r);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        synchronized (this) {
            if (queue.isEmpty() && scheduledFuture != null) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
            }
        }
    }

}
