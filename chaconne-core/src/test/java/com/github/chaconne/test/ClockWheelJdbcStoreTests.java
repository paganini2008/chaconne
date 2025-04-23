package com.github.chaconne.test;

import java.util.List;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.github.chaconne.ClockWheelScheduler;
import com.github.chaconne.JdbcTaskManager;
import com.github.chaconne.TaskDetail;

/**
 * 
 * @Description: ClockWheelJdbcStoreTests
 * @Author: Fred Feng
 * @Date: 09/04/2025
 * @Version 1.0.0
 */
public class ClockWheelJdbcStoreTests {

    private static DataSource dataSource;

    @BeforeClass
    public static void start() throws Exception {
        dataSource = H2JdbcUtils.initializeDB();
        H2JdbcUtils.createTables(dataSource);
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
        List<TaskDetail> taskDetails =
                jdbcTaskManager.findTaskDetails("default", null, null, 10, 0);
        for (TaskDetail vo : taskDetails) {
            System.out.println(vo.toString());
        }
    }

    @AfterClass
    public static void end() throws Exception {
        Thread.sleep(3000L);
        H2JdbcUtils.dropTables(dataSource);
    }

}
