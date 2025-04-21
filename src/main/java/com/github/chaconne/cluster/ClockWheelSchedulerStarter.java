package com.github.chaconne.cluster;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import com.github.chaconne.ClockWheelScheduler;

/**
 * 
 * @Description: ClockWheelSchedulerStarter
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
public class ClockWheelSchedulerStarter implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ClockWheelScheduler clockWheelScheduler =
                event.getApplicationContext().getBean(ClockWheelScheduler.class);
        TaskMemberLock taskMemberLock = event.getApplicationContext().getBean(TaskMemberLock.class);
        if (taskMemberLock.tryLock()) {
            clockWheelScheduler.start();
        }
    }



}
