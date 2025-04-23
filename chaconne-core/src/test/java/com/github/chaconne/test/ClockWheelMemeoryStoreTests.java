package com.github.chaconne.test;

import org.junit.Test;
import com.github.chaconne.ClockWheelScheduler;

/**
 * 
 * @Description: ClockWheelMemeoryStoreTests
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public class ClockWheelMemeoryStoreTests {

    @Test
    public void testTask() {
        ClockWheelScheduler clockWheel = new ClockWheelScheduler();
        clockWheel.schedule(new TestTask(), "Tomcat");
        clockWheel.start();

        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        clockWheel.close();
    }

}
