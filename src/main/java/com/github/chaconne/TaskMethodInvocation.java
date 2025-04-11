package com.github.chaconne;

/**
 * 
 * @Description: TaskMethodInvocation
 * @Author: Fred Feng
 * @Date: 10/04/2025
 * @Version 1.0.0
 */
public interface TaskMethodInvocation {

    Object invokeTaskMethod(TaskId taskId, String taskClassName, String taskMethodName,
            String initialParameter);

}
