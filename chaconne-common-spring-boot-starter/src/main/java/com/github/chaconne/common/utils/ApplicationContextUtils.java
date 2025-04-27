package com.github.chaconne.common.utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import com.github.chaconne.utils.MapUtils;

/**
 * 
 * @Description: ApplicationContextUtils
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
@SuppressWarnings("unchecked")
@Component
public class ApplicationContextUtils
        implements ApplicationContextAware, BeanFactoryAware, EnvironmentAware, Ordered {

    private static final SpringContextHolder contextHolder = new SpringContextHolder();

    static class SpringContextHolder {

        ApplicationContext applicationContext;
        BeanFactory beanFactory;
        Environment environment;

        public ApplicationContext getApplicationContext() {
            Assert.notNull(applicationContext, "Nullable ApplicationContext.");
            return applicationContext;
        }

        public BeanFactory getBeanFactory() {
            Assert.notNull(beanFactory, "Nullable BeanFactory.");
            return beanFactory;
        }

        public Environment getEnvironment() {
            Assert.notNull(environment, "Nullable Environment.");
            return environment;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        contextHolder.applicationContext = applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        contextHolder.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        contextHolder.environment = environment;
    }

    public static ApplicationContext getApplicationContext() {
        return contextHolder.getApplicationContext();
    }

    public static BeanFactory getBeanFactory() {
        return contextHolder.getBeanFactory();
    }

    public static Environment getEnvironment() {
        return contextHolder.getEnvironment();
    }

    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return getApplicationContext().getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return getApplicationContext().getBean(name, requiredType);
    }

    public static String[] getBeanNames() {
        return getApplicationContext().getBeanDefinitionNames();
    }

    public static <T> T autowireBean(T object) {
        getApplicationContext().getAutowireCapableBeanFactory().autowireBean(object);
        return object;
    }

    public static String getRequiredProperty(String key) {
        return getEnvironment().getRequiredProperty(key);
    }

    public static <T> T getProperty(String key, Class<T> requiredType) {
        return getEnvironment().getProperty(key, requiredType);
    }

    public static <T> T getProperty(String key, Class<T> requiredType, T defaultValue) {
        return getEnvironment().getProperty(key, requiredType, defaultValue);
    }

    public static String getProperty(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    public static <T extends ApplicationEvent> void publishEvent(T event) {
        getApplicationContext().publishEvent(event);
    }

    public static void setAware(Object bean) {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(getApplicationContext());
        }
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(getBeanFactory());
        }
        if (bean instanceof EnvironmentAware) {
            ((EnvironmentAware) bean).setEnvironment(getEnvironment());
        }
    }

    private static final Map<Type, Object> detachedBeanCache = new HashMap<>();

    public static <T> T getOrCreateBean(Class<T> requiredType, Object... arguments) {
        try {
            return getBean(requiredType);
        } catch (NoSuchBeanDefinitionException e) {
            return (T) MapUtils.getOrCreate(detachedBeanCache, requiredType, () -> {
                try {
                    Object object = ConstructorUtils.invokeConstructor(requiredType, arguments);
                    return autowireBean(object);
                } catch (Exception e1) {
                    throw new NoSuchBeanDefinitionException(requiredType);
                }
            });
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
