package com.github.chaconne;

import java.time.LocalDateTime;

/**
 * 
 * @Description: TaskDetail
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface TaskDetail {

    Task getTask();

    String getInitialParameter();

    TaskStatus getTaskStatus();

    LocalDateTime getNextFiredDateTime();

    LocalDateTime getPreviousFiredDateTime();

    LocalDateTime getLastModified();

    default boolean isUnavailable() {
        return getTaskStatus() == TaskStatus.FINISHED || getTaskStatus() == TaskStatus.CANCELED
                || getTaskStatus() == TaskStatus.PAUSED;
    }

}
