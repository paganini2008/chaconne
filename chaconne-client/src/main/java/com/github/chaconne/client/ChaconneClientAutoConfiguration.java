package com.github.chaconne.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import com.github.chaconne.common.ApplicationContextUtils;
import com.github.chaconne.common.LoggingRequestInterceptor;
import com.github.chaconne.common.MetadataInfo;
import com.github.chaconne.common.RetryableRestTemplate;
import com.github.chaconne.common.SyncRetryer;
import com.github.chaconne.common.TaskMemberRegistration;
import com.github.chaconne.common.lb.Candidate;
import com.github.chaconne.common.lb.LoadBalancedRequestInterceptor;
import com.github.chaconne.common.lb.LoadBalancerManager;
import com.github.chaconne.common.lb.SimpleNameResolver;
import com.github.chaconne.common.lb.StaticLoadBalanceManager;

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

    @ConditionalOnMissingBean(LoadBalancerManager.class)
    @Bean
    public StaticLoadBalanceManager staticLoadBalanceManager() {
        return new StaticLoadBalanceManager(
                new SimpleNameResolver(chaconneClientProperties.getServiceId(),
                        chaconneClientProperties.getServerAddresses()));
    }

    @Bean
    public TaskMemberRegistration taskExecutorRegistration(
            @Autowired(required = false) MetadataInfo metadataInfo) {
        RemoteTaskExecutorRegistration taskExecutorRegistration =
                new RemoteTaskExecutorRegistration();
        taskExecutorRegistration.setChaconneClientProperties(chaconneClientProperties);
        taskExecutorRegistration.setMetadataInfo(metadataInfo);
        return taskExecutorRegistration;
    }

    @Bean
    public RetryableRestTemplate retryableRestTemplate(
            List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors) {
        RetryableRestTemplate retryableRestTemplate = new RetryableRestTemplate(
                new BufferingClientHttpRequestFactory(clientHttpRequestFactory()));
        retryableRestTemplate.getInterceptors().addAll(clientHttpRequestInterceptors);
        retryableRestTemplate.getInterceptors().add(new LoggingRequestInterceptor());
        return retryableRestTemplate;
    }

    @ConditionalOnMissingBean
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        return new SimpleClientHttpRequestFactory();
    }

    @Bean
    public <T extends Candidate> ClientHttpRequestInterceptor loadBalancedRequestInterceptor(
            LoadBalancerManager<T> loadBalanceManager) {
        return new LoadBalancedRequestInterceptor<T>(loadBalanceManager);
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public SyncRetryer syncRetryer() {
        return new SyncRetryer();
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskExecutorRestService taskExecutorRestTemplate(RetryableRestTemplate restTemplate) {
        return new TaskExecutorRestServiceImpl(restTemplate);
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public TaskAnnotationBeanFinder taskAnnotationBeanFinder() {
        return new TaskAnnotationBeanFinder();
    }

}
