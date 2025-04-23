package com.github.chaconne;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.util.ClassUtils;
import com.github.chaconne.cluster.TaskInvocation;
import com.github.chaconne.cluster.TaskInvocationException;
import com.github.chaconne.utils.BeanUtils;
import com.github.chaconne.utils.MapUtils;

/**
 * 
 * @Description: DefaultTaskInvocation
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
@SuppressWarnings("all")
public class DefaultTaskInvocation implements TaskInvocation {

    private final Map<String, Object> taskObjects = new ConcurrentHashMap<String, Object>();
    private final Map<TaskId, Method> taskMethods = new ConcurrentHashMap<TaskId, Method>();

    @Override
    public Task retrieveTaskObject(String taskClassName, Object taskDetail) {
        return (Task) MapUtils.getOrCreate(taskObjects, taskClassName,
                () -> createTaskObject(taskClassName, taskDetail));
    }

    private Object createTaskObject(String taskClassName, Object taskDetail) {
        Class<?> clz = findClass(taskClassName);
        if (!clz.isAssignableFrom(Task.class) || clz.isAssignableFrom(CustomTask.class)) {
            return new CustomTaskImpl(
                    taskDetail instanceof Map ? (Map) taskDetail : BeanUtils.bean2Map(taskDetail),
                    this);
        }
        return createTaskObject(clz);
    }

    @Override
    public Object invokeTaskMethod(TaskId taskId, String taskClassName, String taskMethodName,
            String initialParameter) {
        Class<?> clz = findClass(taskClassName);
        Object taskObject = createTaskObject(clz);
        if (taskObject == null) {
            throw new TaskInvocationException(
                    "Task Object is not found, Task class: " + taskClassName);
        }
        Method method = MapUtils.getOrCreate(taskMethods, taskId,
                () -> findMethod(taskClassName, taskMethodName));
        try {
            return method.invoke(taskObject, initialParameter);
        } catch (InvocationTargetException e) {
            throw new TaskInvocationException(e.getMessage(), e.getTargetException());
        } catch (Exception e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
    }

    private Method findMethod(String taskClassName, String taskMethodName) {
        Class<?> clz = findClass(taskClassName);
        Method method = MethodUtils.getMatchingAccessibleMethod(clz, taskMethodName, String.class);
        method.setAccessible(true);
        return method;
    }

    private Class<?> findClass(String taskClassName) {
        try {
            return ClassUtils.forName(taskClassName, null);
        } catch (ClassNotFoundException e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
    }

    protected Object createTaskObject(Class<?> clz) {
        try {
            return ConstructorUtils.invokeConstructor(clz);
        } catch (Exception e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
    }

}
