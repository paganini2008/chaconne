package com.github.chaconne.cluster;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import com.github.chaconne.UpcomingTaskQueue;
import com.github.chaconne.utils.NetUtils;
import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
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

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.servlet.context-path:}")
    private String servletContextPath;

    @Value("${spring.mvc.servlet.path:}")
    private String mvcContextPath;

    @ConditionalOnMissingBean
    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        config.setInstanceName(applicationName);
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(15701);
        networkConfig.setPortAutoIncrement(true);
        InterfacesConfig interfaceConfig = networkConfig.getInterfaces();
        interfaceConfig.setEnabled(true).addInterface(NetUtils.getLocalAddress().getHostAddress());
        config.getMemberAttributeConfig().setAttributes(clusterInfo());
        return config;
    }

    @ConditionalOnMissingBean
    @Bean
    public ClusterInfo clusterInfo() {
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setGroup(applicationName);
        clusterInfo.setMemberId(UUID.randomUUID().toString());
        clusterInfo.setHost(NetUtils.getLocalAddress().getHostAddress());
        clusterInfo.setPort(serverPort);
        clusterInfo.setContextPath(servletContextPath + mvcContextPath);
        return clusterInfo;
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig());
        return hazelcastInstance;
    }

    @Bean
    public TaskMemberRegistration taskMemberRegistration() {
        return new TaskSchedulerRegistration();
    }



    @Bean
    public TaskMemberManager taskMemberManager(HazelcastInstance hazelcastInstance) {
        RemoteTaskMemberManager taskMemberManager = new RemoteTaskMemberManager(hazelcastInstance);
        return taskMemberManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public UpcomingTaskQueue upcomingTaskQueue(HazelcastInstance hazelcastInstance) {
        return new HazelcastTaskQueue(hazelcastInstance);
    }

    @ConditionalOnMissingBean(LoadBalancedManager.class)
    @Bean
    public TaskMemberLoadBalancedManager loadBalancedManager() {
        return new TaskMemberLoadBalancedManager();
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskSchedulerRestService taskSchedulerRestService(
            LoadBalancedManager<TaskMember> loadBalancedManager,
            ClientHttpRequestFactory requestFactory) {
        return new TaskSchedulerRestTemplate(loadBalancedManager, requestFactory);
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
    public TaskInvocation taskInvocation(TaskSchedulerRestService taskSchedulerRestService) {
        return new RemoteTaskInvocation(taskSchedulerRestService);
    }

}
