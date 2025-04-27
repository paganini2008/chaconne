package com.github.chaconne;

import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.cronsmith.scheduler.ErrorHandler;

/**
 * 
 * @Description: LoggingErrorHandler
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class LoggingErrorHandler implements ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingErrorHandler.class);

    @Override
    public void onHandleScheduler(Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onHandleTask(ZonedDateTime datetime, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onHandleTaskResult(ZonedDateTime datetime, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error(e.getMessage(), e);
        }
    }

}
