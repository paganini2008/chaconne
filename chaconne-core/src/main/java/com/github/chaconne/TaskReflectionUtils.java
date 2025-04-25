package com.github.chaconne;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import com.github.chaconne.utils.MapUtils;

/**
 * 
 * @Description: TaskReflectionUtils
 * @Author: Fred Feng
 * @Date: 25/04/2025
 * @Version 1.0.0
 */
public abstract class TaskReflectionUtils {

    private static final Map<String, Class<?>> taskClasses =
            new ConcurrentHashMap<String, Class<?>>();
    private static final Map<String, Object> taskObjects = new ConcurrentHashMap<String, Object>();
    private static final Map<TaskId, Method> taskMethods = new ConcurrentHashMap<TaskId, Method>();

    private static final CustomTaskFactory defaultCustomTaskFactory =
            new DefaultCustomTaskFactory();

    public static Task getTaskObject(String taskClassName, Map<String, Object> record) {
        Class<?> taskClass = getTaskClass(taskClassName);
        if (!Task.class.isAssignableFrom(taskClass)
                || CustomTask.class.isAssignableFrom(taskClass)) {
            return defaultCustomTaskFactory.createTaskObject(record);
        }
        return (Task) getTaskObject(taskClassName);
    }

    public static Object getTaskObject(String taskClassName) {
        return MapUtils.getOrCreate(taskObjects, taskClassName,
                () -> doGetTaskObject(taskClassName));
    }

    private static Object doGetTaskObject(String taskClassName) {
        Class<?> taskClass = getTaskClass(taskClassName);
        try {
            return ConstructorUtils.invokeConstructor(taskClass);
        } catch (Exception e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
    }

    public static Method getTaskMethod(TaskId taskId, String taskClassName, String taskMethodName) {
        return MapUtils.getOrCreate(taskMethods, taskId,
                () -> doGetTaskMethod(taskClassName, taskMethodName));
    }

    private static Method doGetTaskMethod(String taskClassName, String taskMethodName) {
        Class<?> taskClass = getTaskClass(taskClassName);
        Method method =
                MethodUtils.getMatchingAccessibleMethod(taskClass, taskMethodName, String.class);
        method.setAccessible(true);
        return method;
    }

    public static Class<?> getTaskClass(String taskClassName) {
        return MapUtils.getOrCreate(taskClasses, taskClassName,
                () -> doGetTaskClass(taskClassName));
    }

    private static Class<?> doGetTaskClass(String taskClassName) {
        try {
            return ClassUtils.getClass(taskClassName, false);
        } catch (ClassNotFoundException e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
    }
}
