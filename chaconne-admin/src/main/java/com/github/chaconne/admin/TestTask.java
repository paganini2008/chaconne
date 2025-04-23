package com.github.chaconne.admin;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.github.chaconne.client.Task;

/**
 * 
 * @Description: TestTask
 * @Author: Fred Feng
 * @Date: 22/04/2025
 * @Version 1.0.0
 */
@Component
public class TestTask {

    private static final Logger log = LoggerFactory.getLogger(TestTask.class);

    @Task(cron = "0/5 * * * * ?", description = "Just a test!!!",
            initialParameter = "12311111111111111111")
    public void run1(String msg) {
        log.info("Test: " + LocalDateTime.now() + ", Msg: " + msg);
    }

}
