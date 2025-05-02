package com.github.chaconne.cluster;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import com.github.chaconne.TimeWheelScheduler;

/**
 * 
 * @Description: TimeWheelSchedulerStarter
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
public class TimeWheelSchedulerStarter
        implements SmartApplicationListener, ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        TaskMemberLock taskMemberLock = applicationContext.getBean(TaskMemberLock.class);
        if (taskMemberLock.tryLock()) {
            prepareOnTaskMemberLocked();
        }
    }

    protected void prepareOnTaskMemberLocked() {
        TimeWheelScheduler timeWheelScheduler =
                applicationContext.getBean(TimeWheelScheduler.class);
        timeWheelScheduler.start();
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(ApplicationReadyEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }

}
