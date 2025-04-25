package com.github.chaconne;

import java.time.LocalDateTime;

/**
 * 
 * @Description: TaskListener
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface TaskListener {

    default void onTaskScheduled(LocalDateTime ldt, TaskDetail taskDetail) {};

    default void onTaskBegan(LocalDateTime ldt, TaskDetail taskDetail) {}

    default void onTaskEnded(LocalDateTime ldt, TaskDetail taskDetail, Throwable e) {}

    default void onTaskCanceled(TaskDetail taskDetail) {}

    default void onTaskFinished(TaskDetail taskDetail) {};

}
