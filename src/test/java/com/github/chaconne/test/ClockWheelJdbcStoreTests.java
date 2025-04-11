package com.github.chaconne.test;

import java.util.List;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.github.chaconne.ClockWheelScheduler;
import com.github.chaconne.JdbcTaskManager;
import com.github.chaconne.TaskInfoVo;

/**
 * 
 * @Description: ClockWheelJdbcStoreTests
 * @Author: Fred Feng
 * @Date: 09/04/2025
 * @Version 1.0.0
 */
public class ClockWheelJdbcStoreTests {

    private DataSource dataSource;

    @Before
    public void start() throws Exception {
        dataSource = JdbcUtils.initializeDB();
        JdbcUtils.createTables(dataSource);
    }

    @Test
    public void testA() {
        ClockWheelScheduler clockWheel = new ClockWheelScheduler();
        clockWheel.setTaskManager(new JdbcTaskManager(dataSource));
        clockWheel.schedule(new TestTask(), "Tomcat");
        clockWheel.start();
        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        clockWheel.close();
    }

    @Test
    public void testB() {
        JdbcTaskManager jdbcTaskManager = new JdbcTaskManager(dataSource);
        List<TaskInfoVo> taskInfos = jdbcTaskManager.findTaskInfos("default", null, 10, 0);
        for (TaskInfoVo vo : taskInfos) {
            System.out.println(vo.getTaskName() + "\t" + vo.getTaskGroup() + "\t" + vo.getCron());
        }
    }

    @After
    public void end() throws Exception {
        JdbcUtils.dropTables(dataSource);
    }

}
