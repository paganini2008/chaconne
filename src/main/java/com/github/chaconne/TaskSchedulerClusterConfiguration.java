package com.github.chaconne;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.chaconne.utils.NetUtils;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: TaskSchedulerClusterConfiguration
 * @Author: Fred Feng
 * @Date: 20/04/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class TaskSchedulerClusterConfiguration {


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
        config.getNetworkConfig().setPortAutoIncrement(true);
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
        LocalTaskMemberManager taskMemberManager = new LocalTaskMemberManager();
        hazelcastInstance.getCluster().addMembershipListener(taskMemberManager);
        return taskMemberManager;
    }

    @ConditionalOnMissingBean(LoadBalancedManager.class)
    @Bean
    public TaskMemberLoadBalancedManager loadBalancedManager() {
        return new TaskMemberLoadBalancedManager();
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskInvocation taskInvocation() {
        return new ManagedTaskInvocation();
    }

}
