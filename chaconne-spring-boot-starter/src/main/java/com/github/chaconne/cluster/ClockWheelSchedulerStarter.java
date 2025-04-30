package com.github.chaconne.cluster;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import com.github.chaconne.ClockWheelScheduler;
import com.github.chaconne.CustomTask;
import com.github.chaconne.CustomTaskFactory;

/**
 * 
 * @Description: TaskAnnotationBeanPropcessor
 * @Author: Fred Feng
 * @Date: 22/04/2025
 * @Version 1.0.0
 */
public class ClockWheelSchedulerStarter implements BeanPostProcessor, SmartApplicationListener,
        EnvironmentAware, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ClockWheelSchedulerStarter.class);

    private final List<Map<String, Object>> taskDefinitions = new ArrayList<>();

    private Environment environment;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

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
            taskDefinitions.add(createTaskDefinition(task, method));
        }
        return bean;
    }

    private Map<String, Object> createTaskDefinition(com.github.chaconne.client.Task taskAnnotation,
            Method method) {
        Map<String, Object> info = new HashMap<String, Object>();
        String applicationName = environment.getRequiredProperty("spring.application.name");
        info.put("taskGroup",
                StringUtils.isNotBlank(taskAnnotation.group()) ? taskAnnotation.group()
                        : applicationName);
        info.put("taskName",
                StringUtils.isNotBlank(taskAnnotation.name()) ? taskAnnotation.name()
                        : String.format("%s_%s", method.getDeclaringClass().getSimpleName(),
                                method.getName()));
        info.put("taskClass", method.getDeclaringClass().getName());
        info.put("taskMethod", method.getName());
        info.put("url", StringUtils.isNotBlank(taskAnnotation.url()) ? taskAnnotation.url()
                : String.format("lb://%s", applicationName));
        info.put("description", taskAnnotation.description());
        info.put("cronExpression", taskAnnotation.cron());
        info.put("timeout", taskAnnotation.timeout());
        info.put("maxRetryCount", taskAnnotation.maxRetryCount());
        info.put("initialParameter", taskAnnotation.initialParameter());
        return info;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        ClockWheelScheduler clockWheelScheduler =
                applicationContext.getBean(ClockWheelScheduler.class);
        TaskMemberLock taskMemberLock = applicationContext.getBean(TaskMemberLock.class);
        if (taskMemberLock.tryLock()) {
            CustomTaskFactory customTaskFactory =
                    applicationContext.getBean(CustomTaskFactory.class);
            for (Map<String, Object> taskDefinition : taskDefinitions) {
                try {
                    CustomTask customTask = customTaskFactory.createTaskObject(taskDefinition);
                    clockWheelScheduler.schedule(customTask,
                            (String) taskDefinition.get("initialParameter"));
                    log.info("Save CustomTask: {}", customTask.toString());
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            clockWheelScheduler.start();
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(ApplicationReadyEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }
}
