package com.github.chaconne.client;

import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @Description: TaskAnnotationBeanPropcessor
 * @Author: Fred Feng
 * @Date: 10/04/2025
 * @Version 1.0.0
 */
public class TaskAnnotationBeanPropcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        List<Method> methodList = MethodUtils.getMethodsListWithAnnotation(targetClass, Task.class);
        if (CollectionUtils.isEmpty(methodList)) {
            return bean;
        }
        for (Method method : methodList) {

        }
        return bean;
    }

}
