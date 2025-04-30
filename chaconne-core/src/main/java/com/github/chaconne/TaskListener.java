package com.github.chaconne;

import java.time.ZonedDateTime;

/**
 * 
 * @Description: TaskListener
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface TaskListener {

    default void onTaskScheduled(ZonedDateTime scheduledDateTime, TaskDetail taskDetail) {};

    default void onTaskTriggered(ZonedDateTime firedDateTime, TaskDetail taskDetail) {}

    default void onTaskBegan(ZonedDateTime firedDateTime, TaskDetail taskDetail) {}

    default void onTaskEnded(ZonedDateTime firedDateTime, TaskDetail taskDetail, Object returnValue,
            Throwable e) {}

    default void onTaskCanceled(TaskDetail taskDetail) {}

    default void onTaskFinished(TaskDetail taskDetail) {};

}
