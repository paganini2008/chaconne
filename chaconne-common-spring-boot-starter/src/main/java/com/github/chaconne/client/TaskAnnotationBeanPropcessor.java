package com.github.chaconne.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;
import com.github.chaconne.common.utils.FinalRetryer;

/**
 * 
 * @Description: TaskAnnotationBeanPropcessor
 * @Author: Fred Feng
 * @Date: 10/04/2025
 * @Version 1.0.0
 */
public class TaskAnnotationBeanPropcessor extends FinalRetryer
        implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent>, EnvironmentAware {

    private static final Logger log = LoggerFactory.getLogger(TaskAnnotationBeanPropcessor.class);

    private final List<CreateTaskRequest> createTaskRequests = new ArrayList<>();

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        List<Method> methodList = MethodUtils.getMethodsListWithAnnotation(targetClass, Task.class);
        if (CollectionUtils.isEmpty(methodList)) {
            return bean;
        }
        for (Method method : methodList) {
            Task taskAnnotation = method.getAnnotation(Task.class);
            CreateTaskRequest createTaskRequest = new CreateTaskRequest();
            createTaskRequest.setTaskGroup(
                    StringUtils.isNotBlank(taskAnnotation.group()) ? taskAnnotation.group()
                            : environment.getRequiredProperty("spring.application.name"));
            createTaskRequest.setTaskName(
                    StringUtils.isNotBlank(taskAnnotation.name()) ? taskAnnotation.name()
                            : String.format("%s_%s", method.getDeclaringClass().getSimpleName(),
                                    method.getName()));
            createTaskRequest.setTaskClass(targetClass.getName());
            createTaskRequest.setTaskMethod(method.getName());
            createTaskRequest
                    .setUrl(StringUtils.isNotBlank(taskAnnotation.url()) ? taskAnnotation.url()
                            : String.format("lb://%s",
                                    environment.getRequiredProperty("spring.application.name")));
            createTaskRequest.setDescription(taskAnnotation.description());
            createTaskRequest.setCronExpression(taskAnnotation.cron());
            createTaskRequest.setMaxRetryCount(taskAnnotation.maxRetryCount());
            createTaskRequest.setTimeout(taskAnnotation.timeout());
            createTaskRequest.setInitialParameter(taskAnnotation.initialParameter());
            createTaskRequests.add(createTaskRequest);
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        TaskExecutorRestService taskExecutorRestService =
                event.getApplicationContext().getBean(TaskExecutorRestService.class);
        for (CreateTaskRequest request : createTaskRequests) {
            Runnable r = () -> {
                ApiResponse<Boolean> apiResponse =
                        taskExecutorRestService.saveTask(request).getBody();
                if (apiResponse.getData().booleanValue()) {
                    taskExecutorRestService.scheduleTask(request);
                }
            };
            try {
                r.run();
            } catch (ExhaustedRetryException e) {
                if (e.getCause() instanceof ResourceAccessException) {
                    retry(r);
                } else {
                    if (log.isErrorEnabled()) {
                        log.error(e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }


}
