package com.github.chaconne.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.chaconne.common.AsyncRetryer.AsyncRetryContext;

/**
 * 
 * @Description: AsyncRetryLogger
 * @Author: Fred Feng
 * @Date: 24/05/2025
 * @Version 1.0.0
 */
public class AsyncRetryLogger implements AsyncRetryListener {

    private static final Logger log = LoggerFactory.getLogger(AsyncRetryLogger.class);

    @Override
    public void begin(AsyncRetryContext retryContext) {
        if (log.isInfoEnabled()) {
            log.info("Retry begin: {}", retryContext.toString());
        }
    }

    @Override
    public void end(AsyncRetryContext retryContext) {
        if (log.isInfoEnabled()) {
            log.info("Retry end: {}", retryContext.toString());
        }
    }

    @Override
    public void onSuccess(AsyncRetryContext retryContext, Object returnValue) {
        if (log.isInfoEnabled()) {
            log.info("Retry successed: {}", retryContext.toString());
        }
    }

    @Override
    public void onError(AsyncRetryContext retryContext) {
        if (log.isErrorEnabled()) {
            log.error("Retry failed: {}", retryContext.toString(), retryContext.getCause());
        }
    }

}
