package com.github.chaconne;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.reflect.MethodUtils;
import com.github.chaconne.utils.ExceptionUtils;

/**
 * 
 * @Description: TaskProxy
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public class TaskProxy implements InvocationHandler {

    private Method callbackMethod;
    private final ZonedDateTime datetime;
    private final TaskDetail taskDetail;
    private final Object proxyObject;
    private final ExecutorService executorService;
    private final List<TaskListener> taskListeners;
    private final ErrorHandler errorHandler;

    TaskProxy(ZonedDateTime datetime, TaskDetail taskDetail, ExecutorService executorService,
            List<TaskListener> taskListeners, ErrorHandler errorHandler) {
        this.datetime = datetime;
        this.taskDetail = taskDetail;
        try {
            this.callbackMethod =
                    MethodUtils.getMatchingAccessibleMethod(taskDetail.getTask().getClass(),
                            "handleResult", new Class<?>[] {Object.class, Throwable.class});
        } catch (Exception e) {
        }
        this.proxyObject = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {Task.class}, this);
        this.executorService =
                executorService != null ? executorService : ForkJoinPool.commonPool();
        this.taskListeners = taskListeners;
        this.errorHandler = errorHandler;
    }

    public Task getProxyObject() {
        return (Task) proxyObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Task.DEFAULT_METHOD_NAME.equals(method.getName())) {
            taskListeners.forEach(l -> {
                l.onTaskBegan(datetime, taskDetail);
            });
            Object returnValue = null;
            Throwable thrown = null;
            try {
                Future<Object> future = executorService.submit(() -> {
                    return method.invoke(taskDetail.getTask(), args);
                });
                if (taskDetail.getTask().getTimeout() > 0) {
                    returnValue =
                            future.get(taskDetail.getTask().getTimeout(), TimeUnit.MILLISECONDS);
                } else {
                    returnValue = future.get();
                }

            } catch (Throwable e) {
                thrown = ExceptionUtils.getOriginalException(e);
                errorHandler.onHandleTask(datetime, thrown);
                throw e;
            } finally {
                handleReturnValue(returnValue, thrown);
            }
            return returnValue;
        }
        return method.invoke(taskDetail.getTask(), args);
    }

    private void handleReturnValue(Object returnValue, Throwable thrown) {
        executorService.execute(() -> {
            try {
                if (callbackMethod != null) {
                    callbackMethod.invoke(taskDetail.getTask(), returnValue, thrown);
                }
            } catch (Throwable e) {
                errorHandler.onHandleTaskResult(datetime, e);
            } finally {
                taskListeners.forEach(l -> {
                    l.onTaskEnded(datetime, taskDetail, returnValue, thrown);
                });
            }
        });
    }

}
