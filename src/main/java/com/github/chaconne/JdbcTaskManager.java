package com.github.chaconne;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import com.github.cronsmith.cron.CronExpression;
import com.github.cronsmith.scheduler.CamelCasedHashMap;
import com.github.cronsmith.scheduler.CronTaskException;
import com.github.cronsmith.scheduler.StringUtils;

/**
 * 
 * @Description: JdbcTaskManager
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public class JdbcTaskManager implements TaskManager {

    private static final ZoneId defaultZoneId = ZoneId.of("UTC");

    private static final String SQL_INSERT_STATEMENT =
            "insert into cron_task_detail(task_name,task_group,task_class,description,cron_expression,cron,next_fired_datetime,max_retry_count,timeout,task_status,initial_parameter) values (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SELECT_COLUMNS =
            "task_name,task_group,task_class,description,cron_expression,next_fired_datetime,prev_fired_datetime,max_retry_count,timeout,task_status,initial_parameter,last_modified";
    private static final String SQL_SELECT_ONE_STATEMENT = String.format(
            "select %s from cron_task_detail where task_name=? and task_group=?", SELECT_COLUMNS);
    private static final String SQL_CHECK_EXISTENCE_STATEMENT =
            "select count(*) from cron_task_detail where task_name=? and task_group=?";
    private static final String SQL_SELECT_NEXT_FIRED_STATEMENT =
            "select task_name,task_group from cron_task_detail where next_fired_datetime between (?,?)";
    private static final String SQL_DELETE_STATEMENT =
            "delete from cron_task_detail where task_name=? and task_group=?";
    private static final String SQL_SET_TASK_STATUS_STATEMENT =
            "update cron_task_detail set task_status=?, last_modified=? where task_name=? and task_group=?";
    private static final String SQL_SET_TASK_NEXT_FIRED_STATEMENT =
            "update cron_task_detail set next_fired_datetime=?, prev_fired_datetime=?, last_modified=? where task_name=? and task_group=?";
    private static final String SQL_COUNT_ALL_STATEMENT =
            "select count(*) from cron_task_detail where 1=1";
    private static final String SQL_SELECT_ALL_STATEMENT =
            String.format("select %s from cron_task_detail where 1=1", SELECT_COLUMNS);

    private final DataSource dataSource;
    private final Map<TaskId, Task> taskObjectCache = new ConcurrentHashMap<>();

    public JdbcTaskManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Task retrieveTaskObject(TaskId taskId, Map<String, Object> info) {
        Task task = taskObjectCache.get(taskId);
        if (task == null) {
            taskObjectCache.putIfAbsent(taskId, createTaskObject(info));
            task = taskObjectCache.get(taskId);
        }
        return task;
    }

    protected Task createTaskObject(Map<String, Object> info) {
        final String taskClassName = (String) info.get("taskClass");
        Class<?> taskClass;
        try {
            taskClass = Class.forName(taskClassName, false,
                    Thread.currentThread().getContextClassLoader());
            return taskClass.isAssignableFrom(CustomTask.class) ? new CustomTask(info)
                    : (Task) taskClass.newInstance();
        } catch (Exception e) {
            throw new CronTaskException(e.getMessage(), e);
        }
    }

    private class JdbcTaskDetail implements TaskDetail {

        private final Map<String, Object> info;

        JdbcTaskDetail(Map<String, Object> info) {
            this.info = info;
        }

        @Override
        public Task getTask() {
            String taskName = (String) info.get("taskName");
            String taskGroup = (String) info.get("taskGroup");
            return retrieveTaskObject(TaskId.of(taskGroup, taskName), info);
        }

        @Override
        public String getInitialParameter() {
            return (String) info.get("initialParameter");
        }

        @Override
        public TaskStatus getTaskStatus() {
            return TaskStatus.valueOf(((String) info.get("taskStatus")).toUpperCase());
        }

        @Override
        public LocalDateTime getNextFiredDateTime() {
            return (LocalDateTime) info.get("nextFiredDateTime");
        }

        @Override
        public LocalDateTime getPreviousFiredDateTime() {
            return (LocalDateTime) info.get("prevFiredDateTime");
        }

        public CronExpression getCronExpression() {
            byte[] bytes = (byte[]) info.get("cronExpression");
            return CronExpression.deserialize(bytes);
        }

    }

    @Override
    public TaskDetail saveTask(Task task, String initialParameter) throws CronTaskException {
        if (hasTask(task.getTaskId())) {
            return getTaskDetail(task.getTaskId());
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm = connection.prepareStatement(SQL_INSERT_STATEMENT)) {
            LocalDateTime nextFired = task.getCronExpression().sync().getNextFiredDateTime();
            psm.setObject(1, task.getTaskId().getName());
            psm.setObject(2, task.getTaskId().getGroup());
            psm.setObject(3, task.getClass().getName());
            psm.setObject(4, task.getDescription());
            psm.setObject(5, task.getCronExpression().serialize());
            psm.setObject(6, task.getCronExpression().toString());
            psm.setObject(7, nextFired);
            psm.setObject(8, task.getMaxRetryCount());
            psm.setObject(9, task.getTimeout());
            psm.setObject(10, TaskStatus.STANDBY.name().toLowerCase());
            psm.setObject(11, initialParameter);
            psm.executeUpdate();
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
        return getTaskDetail(task.getTaskId());
    }

    @Override
    public TaskDetail removeTask(TaskId taskId) throws CronTaskException {
        TaskDetail taskDetail = getTaskDetail(taskId);
        if (taskDetail != null) {
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement psm = connection.prepareStatement(SQL_DELETE_STATEMENT)) {
                psm.setObject(1, taskId.getName());
                psm.setObject(2, taskId.getGroup());
                psm.executeUpdate();
            } catch (SQLException e) {
                throw new CronTaskException(e.getMessage(), e);
            }
        }
        return taskDetail;
    }

    @Override
    public TaskDetail getTaskDetail(TaskId taskId) throws CronTaskException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm = connection.prepareStatement(SQL_SELECT_ONE_STATEMENT)) {
            psm.setObject(1, taskId.getName());
            psm.setObject(2, taskId.getGroup());
            try (ResultSet rs = psm.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> info = toMap(rs);
                    return new JdbcTaskDetail(info);
                }
            }
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
        return null;
    }

    private static Map<String, Object> toMap(ResultSet rs) throws CronTaskException {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            CamelCasedHashMap info = new CamelCasedHashMap(columnCount);
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                String columnLabel = rsmd.getColumnLabel(columnIndex);
                int columnType = rsmd.getColumnType(columnIndex);
                Object value = columnType == Types.BLOB ? rs.getBytes(columnIndex)
                        : rs.getObject(columnIndex);
                info.put(columnLabel, value);
            }
            return info;
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasTask(TaskId taskId) throws CronTaskException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm =
                        connection.prepareStatement(SQL_CHECK_EXISTENCE_STATEMENT)) {
            psm.setObject(1, taskId.getName());
            psm.setObject(2, taskId.getGroup());
            try (ResultSet rs = psm.executeQuery()) {
                if (rs.next()) {
                    Object result = rs.getObject(1);
                    return (result instanceof Number) ? ((Number) result).intValue() > 0 : false;
                }
            }
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public int getTaskCount(String group, String name) throws CronTaskException {
        StringBuilder sql = new StringBuilder(SQL_COUNT_ALL_STATEMENT);
        List<Object> args = new ArrayList<Object>();
        if (StringUtils.isNotBlank(group)) {
            sql.append(" and task_group=?");
            args.add(group);
        }
        if (StringUtils.isNotBlank(name)) {
            sql.append(" and task_name like ?");
            args.add("%" + name + "%");
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                psm.setObject(i + 1, arg);
            }
            try (ResultSet rs = psm.executeQuery()) {
                if (rs.next()) {
                    Object result = rs.getObject(1);
                    return (result instanceof Number) ? ((Number) result).intValue() : 0;
                }
            }
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
        return 0;
    }


    @Override
    public List<TaskInfoVo> findTaskInfos(String group, String name, int limit, int offset)
            throws CronTaskException {
        StringBuilder sql = new StringBuilder(SQL_SELECT_ALL_STATEMENT);
        List<Object> args = new ArrayList<Object>();
        if (StringUtils.isNotBlank(group)) {
            sql.append(" and task_group=?");
            args.add(group);
        }
        if (StringUtils.isNotBlank(name)) {
            sql.append(" and task_name like ?");
            args.add("%" + name + "%");
        }
        if (limit > 0) {
            sql.append(" limit ?");
            args.add(limit);
        }
        if (offset > 0) {
            sql.append(" offset ?");
            args.add(offset);
        }
        List<TaskInfoVo> voList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                psm.setObject(i + 1, arg);
            }
            try (ResultSet rs = psm.executeQuery()) {
                while (rs.next()) {
                    TaskInfoVo vo = new TaskInfoVo();
                    BeanUtils.populateBean(vo, toMap(rs));
                    voList.add(vo);
                }
            }
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
        return voList;
    }

    @Override
    public List<LocalDateTime> findNextFiredDateTimes(TaskId taskId, LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws CronTaskException {
        TaskDetail taskDetail = getTaskDetail(taskId);
        CronExpression cronExpression = ((JdbcTaskDetail) taskDetail).getCronExpression();
        List<LocalDateTime> results = cronExpression.list(startDateTime, endDateTime);
        return results;
    }

    @Override
    public List<TaskId> findUpcomingTasksBetween(LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws CronTaskException {
        List<TaskId> list = new ArrayList<TaskId>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm =
                        connection.prepareStatement(SQL_SELECT_NEXT_FIRED_STATEMENT)) {
            psm.setObject(1, startDateTime);
            psm.setObject(2, endDateTime);
            try (ResultSet rs = psm.executeQuery()) {
                if (rs.next()) {
                    String taskName = rs.getString(1);
                    String taskGroup = rs.getString(2);
                    list.add(TaskId.of(taskGroup, taskName));
                }
            }
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
        return list;
    }

    @Override
    public LocalDateTime computeNextFiredDateTime(TaskId taskId,
            LocalDateTime previousFiredDateTime) throws CronTaskException {
        TaskDetail taskDetail = getTaskDetail(taskId);
        LocalDateTime prevFiredDateTime = taskDetail.getPreviousFiredDateTime();
        LocalDateTime nextFiredDateTime = ((JdbcTaskDetail) taskDetail).getCronExpression()
                .getNextFiredDateTime(taskDetail.getNextFiredDateTime());
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm =
                        connection.prepareStatement(SQL_SET_TASK_NEXT_FIRED_STATEMENT)) {
            psm.setObject(1, nextFiredDateTime);
            psm.setObject(2, prevFiredDateTime);
            psm.setObject(3, LocalDateTime.now(defaultZoneId));
            psm.setObject(4, taskId.getName());
            psm.setObject(5, taskId.getGroup());
            psm.executeUpdate();
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
        return nextFiredDateTime;
    }

    @Override
    public void setTaskStatus(TaskId taskId, TaskStatus status) throws CronTaskException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm =
                        connection.prepareStatement(SQL_SET_TASK_STATUS_STATEMENT)) {
            psm.setObject(1, status.name().toLowerCase());
            psm.setObject(2, LocalDateTime.now(defaultZoneId));
            psm.setObject(3, taskId.getName());
            psm.setObject(4, taskId.getGroup());
            psm.executeUpdate();
        } catch (SQLException e) {
            throw new CronTaskException(e.getMessage(), e);
        }
    }


}
