package com.github.chaconne;

import com.github.cronsmith.cron.CronExpression;

/**
 * 
 * @Description: Task
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface Task {

    String DEFAULT_METHOD_NAME = "execute";

    default TaskId getTaskId() {
        return TaskId.of(getClass().getSimpleName());
    }

    default String getDescription() {
        return "";
    }

    CronExpression getCronExpression();

    default long getTimeout() {
        return -1L;
    }

    default int getMaxRetryCount() {
        return 0;
    }

    Object execute(String initialParameter);

    default void handleResult(Object result, Throwable reason) {}

}
