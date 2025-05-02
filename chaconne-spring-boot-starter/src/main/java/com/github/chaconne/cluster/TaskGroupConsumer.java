package com.github.chaconne.cluster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import com.github.chaconne.TaskId;
import com.github.chaconne.TaskInvocationException;
import com.github.chaconne.TaskReflectionUtils;
import com.github.chaconne.common.RunTaskRequest;
import com.github.chaconne.common.utils.ApplicationContextUtils;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: TaskGroupConsumer
 * @Author: Fred Feng
 * @Date: 22/04/2025
 * @Version 1.0.0
 */
public class TaskGroupConsumer implements Runnable, InitializingBean, DisposableBean {

    private final static Logger log = LoggerFactory.getLogger(TaskGroupConsumer.class);
    private IQueue<RunTaskRequest> queue;

    public TaskGroupConsumer(String group, HazelcastInstance hazelcastInstance) {
        queue = hazelcastInstance.getQueue(group);
    }

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Override
    public void run() {
        while (running.get()) {
            try {
                RunTaskRequest runTaskRequest = queue.take();
                if (runTaskRequest != null) {
                    invokeTaskMethod(
                            TaskId.of(runTaskRequest.getTaskGroup(), runTaskRequest.getTaskName()),
                            runTaskRequest.getTaskClass(), runTaskRequest.getTaskMethod(),
                            runTaskRequest.getInitialParameter());
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    protected Object invokeTaskMethod(TaskId taskId, String taskClassName, String taskMethodName,
            String initialParameter) {
        Class<?> beanClass;
        try {
            beanClass = ClassUtils.forName(taskClassName, null);
        } catch (ClassNotFoundException e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
        Object taskObject = ApplicationContextUtils.getBean(beanClass);
        Method method = TaskReflectionUtils.getTaskMethod(taskId, taskClassName, taskMethodName);
        try {
            return method.invoke(taskObject, initialParameter);
        } catch (InvocationTargetException e) {
            throw new TaskInvocationException(e.getMessage(), e.getTargetException());
        } catch (Exception e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(this);
        thread.setName("TaskConsumerGroup");
        running.set(true);
        thread.start();
        log.info("TaskGroupConsumer is started.");
    }

    @Override
    public void destroy() throws Exception {
        running.set(false);
        log.info("TaskGroupConsumer is destroyed.");
    }

}
