package com.github.chaconne.admin;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.chaconne.common.Task;

/****
 * 
 * @Description: TestTask
 * @Author: Fred Feng
 * @Date: 22/04/2025
 * @Version 1.0.0
 */
// @Component
public class TestTask {

    private static final Logger log = LoggerFactory.getLogger(TestTask.class);

    @Task(cron = "0/5 * * * * ?", description = "Just a test!!!", initialParameter = "First Job!!!")
    public void run1(String msg) {
        log.info("Test: " + LocalDateTime.now() + ", Msg: " + msg);
    }

}
