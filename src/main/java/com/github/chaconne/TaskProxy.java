package com.github.chaconne;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.github.cronsmith.scheduler.ErrorHandler;

/**
 * 
 * @Description: TaskProxy
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public class TaskProxy implements InvocationHandler {

    private Method callbackMethod;
    private final TaskDetail taskDetail;
    private final Object proxyObject;
    private final ExecutorService executorService;
    private final List<TaskListener> taskListeners;
    private final ErrorHandler errorHandler;

    TaskProxy(TaskDetail taskDetail, ExecutorService executorService,
            List<TaskListener> taskListeners, ErrorHandler errorHandler) {
        this.taskDetail = taskDetail;
        try {
            this.callbackMethod = taskDetail.getTask().getClass().getDeclaredMethod("handleResult",
                    Object.class, Throwable.class);
        } catch (Exception e) {
            this.callbackMethod = null;
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
        if ("execute".equals(method.getName())) {
            taskListeners.forEach(l -> {
                l.onTaskBegin(taskDetail);
            });
            Future<Object> future = executorService.submit(() -> {
                return method.invoke(taskDetail.getTask(), args);
            });
            Object returnValue = null;
            Throwable thrown = null;
            try {
                if (taskDetail.getTask().getTimeout() > 0) {
                    returnValue =
                            future.get(taskDetail.getTask().getTimeout(), TimeUnit.MILLISECONDS);
                } else {
                    returnValue = future.get();
                }
            } catch (Throwable e) {
                thrown = e;
                errorHandler.onHandleTask(e);
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
                errorHandler.onHandleResult(e);
            } finally {
                taskListeners.forEach(l -> {
                    l.onTaskEnd(taskDetail, thrown);
                });
            }
        });
    }

}
