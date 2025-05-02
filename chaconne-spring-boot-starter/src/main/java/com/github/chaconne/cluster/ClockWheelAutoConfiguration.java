package com.github.chaconne.cluster;

import java.util.List;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.chaconne.ClockWheelScheduler;
import com.github.chaconne.CustomTaskFactory;
import com.github.chaconne.DefaultExecutorServiceFactory;
import com.github.chaconne.ErrorHandler;
import com.github.chaconne.ExecutorServiceFactory;
import com.github.chaconne.LoggingErrorHandler;
import com.github.chaconne.TaskListener;
import com.github.chaconne.TaskManager;
import com.github.chaconne.UpcomingTaskQueue;

/**
 * 
 * @Description: ClockWheelAutoConfiguration
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class ClockWheelAutoConfiguration {

    @Bean(destroyMethod = "close")
    public ClockWheelScheduler clockWheelScheduler(TaskManager taskManager,
            UpcomingTaskQueue taskQueue, List<TaskListener> taskListeners) {
        ClockWheelScheduler clockWheelScheduler = new ClockWheelScheduler(executorServiceFactory());
        clockWheelScheduler.setTaskManager(taskManager);
        clockWheelScheduler.setTaskQueue(taskQueue);
        clockWheelScheduler.setErrorHandler(errorHandler());
        clockWheelScheduler.getTaskListeners().add(new LoggingTaskListener());
        clockWheelScheduler.getTaskListeners().addAll(taskListeners);
        return clockWheelScheduler;
    }

    @ConditionalOnMissingBean
    @Bean
    public ExecutorServiceFactory executorServiceFactory() {
        return new DefaultExecutorServiceFactory(200);
    }

    @ConditionalOnMissingBean
    @Bean
    public TaskManager taskManager(DSLContext dslContext, CustomTaskFactory customTaskFactory) {
        JooqTaskManager taskManager = new JooqTaskManager(dslContext);
        taskManager.setCustomTaskFactory(customTaskFactory);
        return taskManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public ErrorHandler errorHandler() {
        return new LoggingErrorHandler();
    }

}
