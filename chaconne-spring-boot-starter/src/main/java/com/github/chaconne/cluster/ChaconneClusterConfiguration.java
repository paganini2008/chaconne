package com.github.chaconne.cluster;

import org.jooq.DSLContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import com.github.chaconne.CustomTaskFactory;
import com.github.chaconne.UpcomingTaskQueue;
import com.github.chaconne.common.TaskMember;
import com.github.chaconne.common.utils.ApplicationContextUtils;
import com.github.chaconne.common.utils.NetUtils;
import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
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
    public Config hazelcastConfig(ClusterInfo clusterInfo, HazelcastTaskQueueStore taskQueueStore) {
        MapStoreConfig mapStoreConfig =
                new MapStoreConfig().setEnabled(true).setImplementation(taskQueueStore)
                        .setWriteBatchSize(10).setWriteDelaySeconds(0).setWriteCoalescing(false);
        MapConfig taskQueueMapConfig = new MapConfig(HazelcastTaskQueue.DEFAULT_QUEUE_NAME);
        taskQueueMapConfig.setMapStoreConfig(mapStoreConfig);

        Config config = new Config();
        config.addMapConfig(taskQueueMapConfig);
        config.setInstanceName(applicationName);
        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(15701);
        networkConfig.setPortAutoIncrement(true);
        InterfacesConfig interfaceConfig = networkConfig.getInterfaces();
        interfaceConfig.setEnabled(true).addInterface(NetUtils.getLocalAddress().getHostAddress());
        config.getMemberAttributeConfig().setAttributes(clusterInfo);
        return config;
    }

    @Bean
    public HazelcastTaskQueueStore hazelcastTaskQueueStore(@Lazy DSLContext dslContext) {
        return new HazelcastTaskQueueStore(dslContext);
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

    @Bean
    public TaskLogger taskLogger(DSLContext dslContext) {
        return new TaskLogger(dslContext);
    }

    @ConditionalOnMissingBean
    @Bean
    public UpcomingTaskQueue upcomingTaskQueue(HazelcastInstance hazelcastInstance) {
        return new HazelcastTaskQueue(hazelcastInstance);
    }

    @ConditionalOnMissingBean
    @Bean
    public CustomTaskFactory customTaskFactory(HazelcastInstance hazelcastInstance) {
        return new LocalCustomTaskFactory(hazelcastInstance);
    }

    @Bean
    public TaskGroupConsumer taskGroupConsumer(HazelcastInstance hazelcastInstance) {
        return new TaskGroupConsumer(applicationName, hazelcastInstance);
    }

    @Bean
    public TaskMemberLock taskMemberLock(HazelcastInstance hazelcastInstance) {
        return new TaskMemberLock(hazelcastInstance);
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public TimeWheelSchedulerLocalStarter clockWheelSchedulerStarter() {
        return new TimeWheelSchedulerLocalStarter();
    }

}
