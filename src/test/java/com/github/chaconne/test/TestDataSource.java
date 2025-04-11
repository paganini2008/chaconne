package com.github.chaconne.test;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * 
 * @Description: TestDataSource
 * @Author: Fred Feng
 * @Date: 09/04/2025
 * @Version 1.0.0
 */
public class TestDataSource implements DataSource {

    private String driverClassName;
    private String jdbcUrl;
    private String user;
    private String password;
    private Semaphore semaphore = new Semaphore(8);
    private Boolean autoCommit = true;
    private Integer transactionIsolationLevel;
    private int timeout = 10;

    public TestDataSource() {}

    public int getTimeout() {
        return timeout;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid driverClassName: " + driverClassName, e);
        }
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMaxSize(int maxSize) {
        this.semaphore = new Semaphore(maxSize);
    }

    public void setTransactionIsolationLevel(Integer transactionIsolationLevel) {
        this.transactionIsolationLevel = transactionIsolationLevel;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setTransactionIsolationLevel(int transactionIsolationLevel) {
        this.transactionIsolationLevel = transactionIsolationLevel;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public int getTransactionIsolationLevel() {
        return transactionIsolationLevel;
    }

    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported by " + getClass().getName());
    }

    public void setLoginTimeout(int timeout) throws SQLException {
        throw new UnsupportedOperationException("Not supported by " + getClass().getName());
    }

    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not supported by " + getClass().getName());
    }

    public void setLogWriter(PrintWriter pw) throws SQLException {
        throw new UnsupportedOperationException("Not supported by " + getClass().getName());
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == null) {
            throw new NullPointerException("Interface argument must not be null");
        }
        if (!DataSource.class.isAssignableFrom(iface)) {
            throw new SQLException("DataSource of type [" + getClass().getName()
                    + "] can only be unwrapped as [javax.sql.DataSource], not as ["
                    + iface.getName());
        }
        return iface.cast(this);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return DataSource.class.isAssignableFrom(iface);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by " + getClass().getName());
    }

    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    public void close() throws SQLException {}

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Connection getConnection() throws SQLException {
        try {
            if (timeout > 0) {
                if (semaphore.tryAcquire(timeout, TimeUnit.SECONDS) == false) {
                    throw new SQLException("Acquiring connection timeout for " + timeout + " sec.");
                }
            } else {
                semaphore.acquire();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
        if (autoCommit != null) {
            connection.setAutoCommit(autoCommit);
        }
        if (transactionIsolationLevel != null) {
            connection.setTransactionIsolation(transactionIsolationLevel);
        }
        return new ConnectionProxy(connection, semaphore).getProxyConnection();
    }

    static class ConnectionProxy implements InvocationHandler {

        private static final String CLOSE = "close";
        private static final Class<?>[] IFACES = new Class<?>[] {Connection.class};

        ConnectionProxy(Connection realConnection, Semaphore semaphore) {
            this.realConnection = realConnection;
            this.semaphore = semaphore;
            this.proxyConnection = (Connection) Proxy
                    .newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
        }

        private final Connection realConnection;
        private final Connection proxyConnection;
        private final Semaphore semaphore;

        public Connection getRealConnection() {
            return realConnection;
        }

        public Connection getProxyConnection() {
            return proxyConnection;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("equals")) {
                return (realConnection == args[0]);
            } else if (methodName.equals("hashCode")) {
                return System.identityHashCode(realConnection);
            } else if (methodName.equals("toString")) {
                return realConnection.toString();
            } else if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
                try {
                    realConnection.close();
                } catch (Exception e) {
                }
                semaphore.release();
                return null;
            } else {
                return method.invoke(realConnection, args);
            }
        }
    }
}
