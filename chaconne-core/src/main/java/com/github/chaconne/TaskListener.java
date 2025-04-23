package com.github.chaconne;

/**
 * 
 * @Description: TaskListener
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface TaskListener {

    default void onTaskScheduled(TaskDetail taskDetail) {};

    default void onTaskBegan(TaskDetail taskDetail) {}

    default void onTaskEnded(TaskDetail taskDetail, Throwable e) {}

    default void onTaskCanceled(TaskDetail taskDetail) {}

    default void onTaskFinished(TaskDetail taskDetail) {};

}
