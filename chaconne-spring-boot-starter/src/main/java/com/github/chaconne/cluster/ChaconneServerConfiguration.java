package com.github.chaconne.cluster;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import com.github.chaconne.CustomTaskFactory;
import com.github.chaconne.UpcomingTaskQueue;
import com.github.chaconne.common.LoggingRequestInterceptor;
import com.github.chaconne.common.RetryableRestTemplate;
import com.github.chaconne.common.lb.ApiPing;
import com.github.chaconne.common.lb.Candidate;
import com.github.chaconne.common.lb.LoadBalancedRequestInterceptor;
import com.github.chaconne.common.lb.LoadBalancerManager;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: ChaconneServerConfiguration
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
@Import({TaskManagerEndpoint.class})
@Configuration(proxyBeanMethods = false)
public class ChaconneServerConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public ChaconneConfigurator chaconneConfigurator(
            HazelcastTaskMemberStore hazelcastTaskMemberStore,
            HazelcastTaskQueueStore taskQueueStore) {
        return new ChaconneConfigurator(hazelcastTaskMemberStore, taskQueueStore);
    }

    @Bean
    public HazelcastTaskQueueStore hazelcastTaskQueueStore(@Lazy DSLContext dslContext) {
        return new HazelcastTaskQueueStore(dslContext);
    }

    @Bean
    public HazelcastTaskMemberStore hazelcastTaskMemberStore(@Lazy DSLContext dslContext) {
        return new HazelcastTaskMemberStore(dslContext);
    }

    @Bean
    public TaskLogger taskLogger(@Lazy DSLContext dslContext) {
        return new TaskLogger(dslContext);
    }

    @ConditionalOnMissingBean
    @Bean
    public UpcomingTaskQueue upcomingTaskQueue(HazelcastInstance hazelcastInstance) {
        return new HazelcastTaskQueue(hazelcastInstance);
    }

    @Bean
    public TaskMemberLoadBalancerManager loadBalancedManager(TaskMemberManager taskMemberManager) {
        TaskMemberLoadBalancerManager loadBalancerManager =
                new TaskMemberLoadBalancerManager(taskMemberManager);
        loadBalancerManager.setPing(new ApiPing<>());
        return loadBalancerManager;
    }

    @Bean
    public RetryableRestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory,
            List<ClientHttpRequestInterceptor> interceptors) {
        RetryableRestTemplate restTemplate = new RetryableRestTemplate(
                new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
        restTemplate.getInterceptors().addAll(interceptors);
        return restTemplate;
    }

    @Bean
    public <T extends Candidate> ClientHttpRequestInterceptor loadBalancedRequestInterceptor(
            LoadBalancerManager<T> loadBalanceManager) {
        return new LoadBalancedRequestInterceptor<T>(loadBalanceManager);
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskSchedulerRestService taskSchedulerRestService(RetryableRestTemplate restTemplate) {
        return new TaskSchedulerRestServiceImpl(restTemplate);
    }

    @ConditionalOnMissingBean
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);
        ConnectionConfig connectionConfig =
                ConnectionConfig.custom().setConnectTimeout(10000, TimeUnit.MILLISECONDS)
                        .setSocketTimeout(50000, TimeUnit.MILLISECONDS).build();
        connectionManager.setDefaultConnectionConfig(connectionConfig);
        CloseableHttpClient httpClient =
                HttpClients.custom().setConnectionManager(connectionManager).build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @ConditionalOnMissingBean
    @Bean
    public CustomTaskFactory customTaskFactory(TaskSchedulerRestService taskSchedulerRestService) {
        return new RemoteCustomTaskFactory(taskSchedulerRestService);
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public TimeWheelSchedulerStarter timeWheelSchedulerStarter() {
        return new TimeWheelSchedulerStarter();
    }

}
