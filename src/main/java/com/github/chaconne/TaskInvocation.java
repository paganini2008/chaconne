package com.github.chaconne;

/**
 * 
 * @Description: TaskInvocation
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public interface TaskInvocation {

    Task retrieveTaskObject(String taskClassName, Object taskDetail);

    Object invokeTaskMethod(TaskId taskId, String taskClassName, String taskMethodName,
            String initialParameter);

}
