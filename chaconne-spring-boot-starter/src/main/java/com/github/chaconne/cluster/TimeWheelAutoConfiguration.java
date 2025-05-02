package com.github.chaconne.cluster;

import java.util.List;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.chaconne.CustomTaskFactory;
import com.github.chaconne.DefaultExecutorServiceFactory;
import com.github.chaconne.ErrorHandler;
import com.github.chaconne.ExecutorServiceFactory;
import com.github.chaconne.LoggingErrorHandler;
import com.github.chaconne.TaskListener;
import com.github.chaconne.TaskManager;
import com.github.chaconne.TimeWheelScheduler;
import com.github.chaconne.UpcomingTaskQueue;

/**
 * 
 * @Description: TimeWheelAutoConfiguration
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class TimeWheelAutoConfiguration {

    @Bean(destroyMethod = "close")
    public TimeWheelScheduler timeWheelScheduler(TaskManager taskManager,
            UpcomingTaskQueue taskQueue, List<TaskListener> taskListeners) {
        TimeWheelScheduler timeWheelScheduler = new TimeWheelScheduler(executorServiceFactory());
        timeWheelScheduler.setTaskManager(taskManager);
        timeWheelScheduler.setTaskQueue(taskQueue);
        timeWheelScheduler.setErrorHandler(errorHandler());
        timeWheelScheduler.getTaskListeners().add(new LoggingTaskListener());
        timeWheelScheduler.getTaskListeners().addAll(taskListeners);
        return timeWheelScheduler;
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
