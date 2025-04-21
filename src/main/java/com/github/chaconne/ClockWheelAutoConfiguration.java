package com.github.chaconne;

import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.chaconne.cluster.TaskInvocation;
import com.github.cronsmith.scheduler.DefaultExecutorServiceFactory;
import com.github.cronsmith.scheduler.ErrorHandler;
import com.github.cronsmith.scheduler.ExecutorServiceFactory;

/**
 * 
 * @Description: ClockWheelAutoConfiguration
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class ClockWheelAutoConfiguration {

    @Bean
    public ClockWheelScheduler clockWheelScheduler(TaskManager taskManager,
            UpcomingTaskQueue taskQueue) {
        ClockWheelScheduler clockWheelScheduler = new ClockWheelScheduler(executorServiceFactory());
        clockWheelScheduler.setTaskManager(taskManager);
        clockWheelScheduler.setTaskQueue(taskQueue);
        clockWheelScheduler.setErrorHandler(errorHandler());
        return clockWheelScheduler;
    }

    @ConditionalOnMissingBean
    @Bean
    public ExecutorServiceFactory executorServiceFactory() {
        return new DefaultExecutorServiceFactory(200);
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskManager taskManager(DSLContext dslContext, TaskInvocation taskInvocation) {
        JooqTaskManager taskManager = new JooqTaskManager(dslContext);
        taskManager.setTaskInvocation(taskInvocation);
        return taskManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public ErrorHandler errorHandler() {
        return new LoggingErrorHandler();
    }

}
