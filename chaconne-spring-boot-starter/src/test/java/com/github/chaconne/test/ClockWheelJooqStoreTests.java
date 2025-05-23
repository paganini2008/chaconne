package com.github.chaconne.test;

import java.util.List;
import java.util.Properties;
import org.jooq.SQLDialect;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import com.github.chaconne.TimeWheelScheduler;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskManager;
import com.github.chaconne.cluster.JooqTaskManager;

/**
 * 
 * @Description: ClockWheelJooqStoreTests
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class ClockWheelJooqStoreTests {

    private static TaskManager taskManager;

    @BeforeClass
    public static void start() throws Exception {
        ClassPathResource resource = new ClassPathResource("db.properties");
        Properties dbConfig = new Properties();
        dbConfig.load(resource.getInputStream());
        TestDataSource dataSource = new TestDataSource();
        dataSource.setDriverClassName(dbConfig.getProperty("driverClassName"));
        dataSource.setJdbcUrl(dbConfig.getProperty("jdbcUrl"));
        dataSource.setUser(dbConfig.getProperty("user"));
        dataSource.setPassword(dbConfig.getProperty("password"));
        System.out.println("DataSource info: " + dataSource);

        taskManager = new JooqTaskManager(dataSource, SQLDialect.MYSQL);
    }

    @Test
    public void testA() {
        TimeWheelScheduler clockWheel = new TimeWheelScheduler();
        clockWheel.setTaskManager(taskManager);
        clockWheel.schedule(new TestClusterTask(), "Tomcat");
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
        List<TaskDetail> taskDetails = taskManager.findTaskDetails("default", null, null, 10, 0);
        for (TaskDetail taskDetail : taskDetails) {
            System.out.println(taskDetail.toString());
        }
    }

    @AfterClass
    public static void end() throws Exception {}
}
