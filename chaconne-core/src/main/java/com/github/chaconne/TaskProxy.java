package com.github.chaconne;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
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
    private final LocalDateTime datetime;
    private final TaskDetail taskDetail;
    private final Object proxyObject;
    private final ExecutorService executorService;
    private final List<TaskListener> taskListeners;
    private final ErrorHandler errorHandler;

    TaskProxy(LocalDateTime datetime, TaskDetail taskDetail, ExecutorService executorService,
            List<TaskListener> taskListeners, ErrorHandler errorHandler) {
        this.datetime = datetime;
        this.taskDetail = taskDetail;
        try {
            this.callbackMethod = taskDetail.getTask().getClass().getDeclaredMethod("handleResult",
                    Object.class, Throwable.class);
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
                errorHandler.onHandleTask(datetime, e);
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
                    l.onTaskEnded(datetime, taskDetail, thrown);
                });
            }
        });
    }

}
