package com.github.chaconne.cluster;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.chaconne.common.TaskMember;
import com.github.chaconne.common.utils.NetUtils;
import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: TaskMemberClusterAutoConfiguration
 * @Author: Fred Feng
 * @Date: 28/05/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class TaskMemberClusterAutoConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired(required = false)
    private Configurator configurator;

    @ConditionalOnMissingBean
    @Bean
    public Config config(ClusterInfo clusterInfo) {
        final String hostAddress = NetUtils.getLocalAddress().getHostAddress();
        Config config = new Config();
        config.setInstanceName(applicationName);
        NetworkConfig networkConfig = config.getNetworkConfig();
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        networkConfig.setPort(15701);
        joinConfig.getTcpIpConfig().setEnabled(true).addMember(hostAddress);
        InterfacesConfig interfaceConfig = networkConfig.getInterfaces();
        interfaceConfig.setEnabled(true).addInterface(hostAddress);
        config.getMemberAttributeConfig().setAttributes(clusterInfo);
        if (configurator != null) {
            configurator.applyConfig(config);
        }
        return config;
    }

    @ConditionalOnMissingBean
    @Bean
    public ClusterInfo clusterInfo(TaskMember taskMember) {
        ClusterInfo clusterInfo = new ClusterInfo();
        BeanUtils.copyProperties(taskMember, clusterInfo);
        if (taskMember.getMetadata() != null) {
            clusterInfo.putAll(taskMember.getMetadata());
        }
        return clusterInfo;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(Config hazelcastConfig) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        return hazelcastInstance;
    }


    @Bean
    public TaskMemberLock taskMemberLock(HazelcastInstance hazelcastInstance) {
        return new TaskMemberLock(hazelcastInstance);
    }

    @Bean
    public TaskMembershipEventPublisher taskMembershipEventPublisher(
            HazelcastInstance hazelcastInstance) {
        return new TaskMembershipEventPublisher(hazelcastInstance);
    }

    @Bean
    public LocalTaskMemberRegistration localTaskMemberRegistration() {
        return new LocalTaskMemberRegistration();
    }

}
