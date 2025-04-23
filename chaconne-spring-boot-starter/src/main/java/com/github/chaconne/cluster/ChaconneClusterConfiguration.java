package com.github.chaconne.cluster;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import com.github.chaconne.TaskInvocation;
import com.github.chaconne.UpcomingTaskQueue;
import com.github.chaconne.cluster.utils.ApplicationContextUtils;
import com.github.chaconne.cluster.utils.NetUtils;
import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: ChaconneClusterConfiguration
 * @Author: Fred Feng
 * @Date: 20/04/2025
 * @Version 1.0.0
 */
@Import({ApplicationContextUtils.class})
@Configuration(proxyBeanMethods = false)
public class ChaconneClusterConfiguration {


    @Value("${spring.application.name}")
    private String applicationName;

    @ConditionalOnMissingBean
    @Bean
    public Config hazelcastConfig(ClusterInfo clusterInfo) {
        Config config = new Config();
        config.setInstanceName(applicationName);
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(15701);
        networkConfig.setPortAutoIncrement(true);
        InterfacesConfig interfaceConfig = networkConfig.getInterfaces();
        interfaceConfig.setEnabled(true).addInterface(NetUtils.getLocalAddress().getHostAddress());
        config.getMemberAttributeConfig().setAttributes(clusterInfo);
        return config;
    }

    @ConditionalOnMissingBean
    @Bean
    public ClusterInfo clusterInfo(TaskMember taskMember) {
        ClusterInfo clusterInfo = new ClusterInfo();
        BeanUtils.copyProperties(taskMember, clusterInfo);
        return clusterInfo;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(Config hazelcastConfig) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        return hazelcastInstance;
    }

    @Bean
    public TaskMembershipEventPublisher taskMembershipEventPublisher(
            HazelcastInstance hazelcastInstance) {
        return new TaskMembershipEventPublisher(hazelcastInstance);
    }

    @Bean
    public TaskSchedulerRegistration taskMemberRegistration() {
        return new TaskSchedulerRegistration();
    }

    @Bean
    public TaskMemberManager taskMemberManager(HazelcastInstance hazelcastInstance) {
        LocalTaskMemberManager taskMemberManager = new LocalTaskMemberManager();
        return taskMemberManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public UpcomingTaskQueue upcomingTaskQueue(HazelcastInstance hazelcastInstance) {
        return new HazelcastTaskQueue(hazelcastInstance);
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskInvocation taskInvocation(HazelcastInstance hazelcastInstance) {
        return new LocalTaskInvocation(hazelcastInstance);
    }

    @Bean
    public TaskGroupConsumer taskGroupConsumer(HazelcastInstance hazelcastInstance) {
        return new TaskGroupConsumer(applicationName, hazelcastInstance);
    }

    @Bean
    public TaskMemberLock taskMemberLock(HazelcastInstance hazelcastInstance) {
        return new TaskMemberLock(hazelcastInstance);
    }

    @Bean
    public ClockWheelSchedulerStarter clockWheelSchedulerStarter() {
        return new ClockWheelSchedulerStarter();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public TaskAnnotationBeanPropcessor taskAnnotationBeanPropcessor() {
        return new TaskAnnotationBeanPropcessor();
    }

}
