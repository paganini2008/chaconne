package com.github.chaconne.cluster;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;
import com.github.chaconne.CustomTask;
import com.github.chaconne.CustomTaskImpl;
import com.github.chaconne.TaskManager;

/**
 * 
 * @Description: TaskAnnotationBeanPropcessor
 * @Author: Fred Feng
 * @Date: 22/04/2025
 * @Version 1.0.0
 */
public class TaskAnnotationBeanPropcessor
        implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(TaskAnnotationBeanPropcessor.class);

    private final List<Map<String, Object>> taskInfos = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        List<Method> methodList = MethodUtils.getMethodsListWithAnnotation(targetClass,
                com.github.chaconne.client.Task.class);
        if (CollectionUtils.isEmpty(methodList)) {
            return bean;
        }
        for (Method method : methodList) {
            com.github.chaconne.client.Task task =
                    method.getAnnotation(com.github.chaconne.client.Task.class);
            taskInfos.add(createTaskInfo(task, method));
        }
        return bean;
    }

    private Map<String, Object> createTaskInfo(com.github.chaconne.client.Task taskAnnotation,
            Method method) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("taskGroup", taskAnnotation.group());
        info.put("taskName", taskAnnotation.name());
        info.put("taskClass", method.getDeclaringClass().getName());
        info.put("taskMethod", method.getName());
        info.put("description", taskAnnotation.description());
        info.put("cronExpression", taskAnnotation.cron());
        info.put("timeout", taskAnnotation.timeout());
        info.put("maxRetryCount", taskAnnotation.maxRetryCount());
        info.put("initialParameter", taskAnnotation.initialParameter());
        info.put("updatePolicy", taskAnnotation.updatePolicy());
        return info;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        TaskInvocation taskInvocation = event.getApplicationContext().getBean(TaskInvocation.class);
        TaskManager taskManager = event.getApplicationContext().getBean(TaskManager.class);
        for (Map<String, Object> taskInfo : taskInfos) {
            try {
                CustomTask customTask = new CustomTaskImpl(taskInfo, taskInvocation);
                taskManager.saveTask(customTask, (String) taskInfo.get("initialParameter"));
                log.info("Save CustomTask: {}", customTask.toString());
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
