package com.github.chaconne;

import java.util.Map;

/**
 * 
 * @Description: TaskInvocation
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public interface TaskInvocation {

    Task retrieveTaskObject2(String taskClassName, Map<String, Object> record);

    Object invokeTaskMethod2(TaskId taskId, String taskClassName, String taskMethodName,
            String initialParameter);

}
