package com.github.chaconne.cluster;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import com.github.chaconne.ClockWheelScheduler;

/**
 * 
 * @Description: ClockWheelSchedulerStarter
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
public class ClockWheelSchedulerStarter
        implements SmartApplicationListener, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        ClockWheelScheduler clockWheelScheduler =
                applicationContext.getBean(ClockWheelScheduler.class);
        TaskMemberLock taskMemberLock = applicationContext.getBean(TaskMemberLock.class);
        if (taskMemberLock.tryLock()) {
            clockWheelScheduler.start();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(ApplicationReadyEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }

}
