package com.github.chaconne.common;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import com.github.chaconne.common.utils.ApplicationContextUtils;

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
    public TaskExecutorRestService taskExecutorRestTemplate() {
        return new TaskExecutorRestTemplate(URI.create(chaconneClientProperties.getBaseUrl()));
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public TaskAnnotationBeanFinder taskAnnotationBeanFinder() {
        return new TaskAnnotationBeanFinder();
    }

}
