package com.github.chaconne.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

/**
 * 
 * @Description: RetryLogger
 * @Author: Fred Feng
 * @Date: 24/05/2025
 * @Version 1.0.0
 */
public class RetryLogger implements RetryListener {

    private static final Logger log = LoggerFactory.getLogger(RetryLogger.class);

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context,
            RetryCallback<T, E> callback) {
        if (log.isTraceEnabled()) {
            log.trace("Retry started: {}", context.toString());
        }
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
            Throwable e) {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error("Retry done: {}", context.toString(), e);
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Retry done: {}", context.toString());
            }
        }
    }

    @Override
    public <T, E extends Throwable> void onSuccess(RetryContext context,
            RetryCallback<T, E> callback, T result) {
        if (log.isTraceEnabled()) {
            log.trace("Retry succeed: {}", context.toString());
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
            Throwable e) {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error("Retry failed: {}", context.toString(), context.getLastThrowable());
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Retry failed: {}", context.toString());
            }
        }
    }

}
