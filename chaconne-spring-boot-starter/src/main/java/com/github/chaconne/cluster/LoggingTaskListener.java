package com.github.chaconne.cluster;

import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskListener;

/**
 * 
 * @Description: LoggingTaskListener
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class LoggingTaskListener implements TaskListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingTaskListener.class);

    @Override
    public void onTaskScheduled(ZonedDateTime zdt, TaskDetail taskDetail) {
        if (log.isTraceEnabled()) {
            log.trace("Task Scheduled: {}", taskDetail.toString());
        }
    }

    @Override
    public void onTaskBegan(ZonedDateTime zdt, TaskDetail taskDetail) {
        if (log.isTraceEnabled()) {
            log.trace("Task Began: {}", taskDetail.toString());
        }
    }

    @Override
    public void onTaskEnded(ZonedDateTime zdt, TaskDetail taskDetail, Object returnValue,
            Throwable e) {
        if (e != null) {
            if (log.isErrorEnabled()) {
                log.error("Task Ended: {}", taskDetail.toString(), e);
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Task Ended: {}", taskDetail.toString());
            }
        }
    }

    @Override
    public void onTaskCanceled(TaskDetail taskDetail) {
        if (log.isTraceEnabled()) {
            log.info("Task Canceled: {}", taskDetail.toString());
        }
    }

    @Override
    public void onTaskFinished(TaskDetail taskDetail) {
        if (log.isTraceEnabled()) {
            log.trace("Task Finished: {}", taskDetail.toString());
        }
    }


}
