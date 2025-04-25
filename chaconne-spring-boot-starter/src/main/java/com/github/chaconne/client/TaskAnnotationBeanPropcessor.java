package com.github.chaconne.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @Description: TaskAnnotationBeanPropcessor
 * @Author: Fred Feng
 * @Date: 10/04/2025
 * @Version 1.0.0
 */
public class TaskAnnotationBeanPropcessor
        implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {

    private final List<CreateTaskRequest> createTaskRequests = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        List<Method> methodList = MethodUtils.getMethodsListWithAnnotation(targetClass, Task.class);
        if (CollectionUtils.isEmpty(methodList)) {
            return bean;
        }
        for (Method method : methodList) {
            Task task = method.getAnnotation(Task.class);
            CreateTaskRequest createTaskRequest = new CreateTaskRequest();
            createTaskRequest.setTaskGroup(task.group());
            createTaskRequest.setTaskName(task.name());
            createTaskRequest.setTaskClass(targetClass.getName());
            createTaskRequest.setTaskMethod(method.getName());
            createTaskRequest.setUrl(beanName);
            createTaskRequest.setDescription(task.description());
            createTaskRequest.setCron(task.cron());
            createTaskRequest.setMaxRetryCount(task.maxRetryCount());
            createTaskRequest.setTimeout(task.timeout());
            createTaskRequest.setInitialParameter(task.initialParameter());
            createTaskRequests.add(createTaskRequest);
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        TaskExecutorRestTemplate restTemplate =
                event.getApplicationContext().getBean(TaskExecutorRestTemplate.class);
        for (CreateTaskRequest request : createTaskRequests) {
            restTemplate.saveTask(request);
        }
    }
}
