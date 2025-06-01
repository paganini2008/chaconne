package com.github.chaconne.cluster;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.hazelcast.core.HazelcastInstance;

/**
 * 
 * @Description: RemoteTaskMemberClusterAutoConfiguration
 * @Author: Fred Feng
 * @Date: 28/05/2025
 * @Version 1.0.0
 */
@AutoConfigureAfter(TaskMemberClusterAutoConfiguration.class)
@Import({TaskMemberManagerEndpoint.class})
@Configuration(proxyBeanMethods = false)
public class RemoteTaskMemberClusterAutoConfiguration {

    @Bean
    public TaskMemberManager localTaskMemberManager(HazelcastInstance hazelcastInstance) {
        return new RemoteTaskMemberManager(hazelcastInstance);
    }

}
