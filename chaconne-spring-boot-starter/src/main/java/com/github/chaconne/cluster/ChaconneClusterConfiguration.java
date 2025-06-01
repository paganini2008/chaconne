package com.github.chaconne.cluster;

import org.jooq.DSLContext;
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
import com.github.chaconne.common.ApplicationContextUtils;
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

    @Bean
    public ChaconneConfigurator chaconneConfigurator(HazelcastTaskMemberStore taskMemberStore,
            HazelcastTaskQueueStore taskQueueStore) {
        return new ChaconneConfigurator(taskMemberStore, taskQueueStore);
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

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public TimeWheelSchedulerLocalStarter clockWheelSchedulerStarter() {
        return new TimeWheelSchedulerLocalStarter();
    }

}
