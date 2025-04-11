package com.github.chaconne.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.h2.tools.RunScript;

/**
 * 
 * @Description: JdbcUtils
 * @Author: Fred Feng
 * @Date: 09/04/2025
 * @Version 1.0.0
 */
public abstract class JdbcUtils {

    public static DataSource initializeDB() throws SQLException {
        TestDataSource dataSource = new TestDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        try (Connection connection = dataSource.getConnection();
                Statement sm = connection.createStatement()) {
            System.out.println(connection);
        }
        return dataSource;
    }

    public static void createTables(DataSource dataSource) throws IOException, SQLException {
        try (Connection connection = dataSource.getConnection()) {
            InputStream ins = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("db/migration/V1__Initial_schema.sql");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));
            RunScript.execute(connection, bufferedReader);
        }
    }

    public static void dropTables(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                Statement sm = connection.createStatement()) {
            sm.execute("drop table cron_task_detail");
        }
    }

}
