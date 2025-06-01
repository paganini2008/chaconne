package com.github.chaconne.cluster;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @Description: LocalTaskMemberClusterAutoConfiguration
 * @Author: Fred Feng
 * @Date: 26/05/2025
 * @Version 1.0.0
 */
@AutoConfigureAfter(TaskMemberClusterAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class LocalTaskMemberClusterAutoConfiguration {

    @Bean
    public TaskMemberManager localTaskMemberManager() {
        return new LocalTaskMemberManager();
    }

}
