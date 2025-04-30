package com.github.chaconne;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 
 * @Description: DefaultCustomTaskFactory
 * @Author: Fred Feng
 * @Date: 25/04/2025
 * @Version 1.0.0
 */
public class DefaultCustomTaskFactory implements CustomTaskFactory {

    @Override
    public CustomTask createTaskObject(Map<String, Object> record) {
        return new DefaultCustomTask(record);
    }

    /**
     * 
     * @Description: DefaultCustomTask
     * @Author: Fred Feng
     * @Date: 25/04/2025
     * @Version 1.0.0
     */
    class DefaultCustomTask extends AbstractCustomTask {

        DefaultCustomTask(Map<String, Object> record) {
            super(record);
        }

        @Override
        protected Object invokeTaskMethod(TaskId taskId, String taskClassName,
                String taskMethodName, String initialParameter) {
            Object taskObject = TaskReflectionUtils.getTaskObject(taskClassName);
            Method method =
                    TaskReflectionUtils.getTaskMethod(taskId, taskClassName, taskMethodName);
            try {
                return method.invoke(taskObject, initialParameter);
            } catch (InvocationTargetException e) {
                throw new TaskInvocationException(e.getMessage(), e.getTargetException());
            } catch (Exception e) {
                throw new TaskInvocationException(e.getMessage(), e);
            }
        }

    }

}
