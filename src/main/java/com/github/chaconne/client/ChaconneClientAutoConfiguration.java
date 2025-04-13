package com.github.chaconne.client;

import java.net.URI;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import com.github.chaconne.LoadBalancedManager;
import com.github.chaconne.TaskMemberRegistration;
import com.github.chaconne.UriLoadBalancedManager;
import com.github.chaconne.utils.ApplicationContextUtils;

/**
 * 
 * @Description: ChaconneClientAutoConfiguration
 * @Author: Fred Feng
 * @Date: 17/04/2025
 * @Version 1.0.0
 */
@EnableConfigurationProperties({ChaconneClientProperties.class})
@Import({TaskExecutorEndpoint.class, ApplicationContextUtils.class})
@Configuration(proxyBeanMethods = false)
public class ChaconneClientAutoConfiguration {

    @Autowired
    private ChaconneClientProperties chaconneClientProperties;

    @Bean
    public TaskMemberRegistration taskExecutorRegistration() {
        return new RemoteTaskExecutorRegistration();
    }

    @Bean
    public TaskExecutorRestTemplate taskExecutorRestTemplate(
            LoadBalancedManager<URI> loadBalancedManager) {
        return new TaskExecutorRestTemplate(loadBalancedManager);
    }

    @ConditionalOnMissingBean(LoadBalancedManager.class)
    @Bean
    public UriLoadBalancedManager uriLoadBalancedManager() {
        return new UriLoadBalancedManager(
                Arrays.stream(chaconneClientProperties.getBaseUrls().split(","))
                        .map(str -> URI.create(str)).toArray(l -> new URI[l]));
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public TaskAnnotationBeanPropcessor taskAnnotationBeanPropcessor() {
        return new TaskAnnotationBeanPropcessor();
    }

}
