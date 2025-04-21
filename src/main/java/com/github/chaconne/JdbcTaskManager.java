package com.github.chaconne;

import static com.github.chaconne.Settings.DEFAULT_ZONE_ID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import com.github.chaconne.cluster.TaskInvocation;
import com.github.chaconne.utils.CamelCasedLinkedHashMap;
import com.github.cronsmith.cron.CronExpression;
import com.github.cronsmith.scheduler.StringUtils;

/**
 * 
 * @Description: JdbcTaskManager
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public class JdbcTaskManager implements TaskManager {

    private static final String SQL_INSERT_STATEMENT =
            "insert into cron_task_detail(task_name,task_group,task_class,task_method,description,cron_expression,cron,next_fired_datetime,max_retry_count,timeout,task_status,initial_parameter,last_modified) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE_STATEMENT =
            "update cron_task_detail set task_class=?,task_method=?,description=?,cron_expression=?,cron=?,max_retry_count=?,timeout=?,initial_parameter=?,last_modified=? where task_name=? and task_group=?";
    private static final String SELECT_COLUMNS =
            "task_name,task_group,task_class,task_method,description,cron,next_fired_datetime,prev_fired_datetime,max_retry_count,timeout,task_status,initial_parameter,last_modified";
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
            "update cron_task_detail set next_fired_datetime=?, prev_fired_datetime=?, cron_expression=?, last_modified=? where task_name=? and task_group=?";
    private static final String SQL_COUNT_ALL_STATEMENT =
            "select count(*) from cron_task_detail where 1=1";
    private static final String SQL_SELECT_ALL_STATEMENT =
            String.format("select %s from cron_task_detail where 1=1", SELECT_COLUMNS);

    private final DataSource dataSource;
    private TaskInvocation taskInvocation = new DefaultTaskInvocation();

    public JdbcTaskManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTaskInvocation(TaskInvocation taskInvocation) {
        this.taskInvocation = taskInvocation;
    }

    private class JdbcTaskDetail implements TaskDetail {

        private final Map<String, Object> info;

        JdbcTaskDetail(Map<String, Object> info) {
            this.info = info;
        }

        @Override
        public Task getTask() {
            String taskClassName = (String) info.get("taskClass");
            return taskInvocation.retrieveTaskObject(taskClassName, info);
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

        @Override
        public LocalDateTime getLastModified() {
            return (LocalDateTime) info.get("lastModified");
        }

    }

    @Override
    public TaskDetail saveTask(Task task, String initialParameter) throws ChaconneException {
        if (hasTask(task.getTaskId())) {
            updateTask(task, initialParameter);
        } else {
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement psm = connection.prepareStatement(SQL_INSERT_STATEMENT)) {
                LocalDateTime nextFired = task.getCronExpression().sync().getNextFiredDateTime();
                psm.setObject(1, task.getTaskId().getName());
                psm.setObject(2, task.getTaskId().getGroup());
                psm.setObject(3, task instanceof CustomTask ? ((CustomTask) task).getTaskClassName()
                        : task.getClass().getName());
                psm.setObject(4,
                        task instanceof CustomTask ? ((CustomTask) task).getTaskMethodName()
                                : Task.DEFAULT_METHOD_NAME);
                psm.setObject(5, task.getDescription());
                psm.setBytes(6, task.getCronExpression().serialize());
                psm.setObject(7, task.getCronExpression().toString());
                psm.setObject(8, nextFired);
                psm.setObject(9, task.getMaxRetryCount());
                psm.setObject(10, task.getTimeout());
                psm.setObject(11, TaskStatus.STANDBY.name().toLowerCase());
                psm.setObject(12, initialParameter);
                psm.setObject(13, LocalDateTime.now(DEFAULT_ZONE_ID));
                psm.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new ChaconneException(e.getMessage(), e);
            }
        }
        return getTaskDetail(task.getTaskId());
    }

    private void updateTask(Task task, String initialParameter) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm = connection.prepareStatement(SQL_UPDATE_STATEMENT)) {
            psm.setObject(1, task instanceof CustomTask ? ((CustomTask) task).getTaskClassName()
                    : task.getClass().getName());
            psm.setObject(2, task instanceof CustomTask ? ((CustomTask) task).getTaskMethodName()
                    : Task.DEFAULT_METHOD_NAME);
            psm.setObject(3, task.getDescription());
            psm.setBytes(4, task.getCronExpression().serialize());
            psm.setObject(5, task.getCronExpression().toString());
            psm.setObject(6, task.getMaxRetryCount());
            psm.setObject(7, task.getTimeout());
            psm.setObject(8, initialParameter);
            psm.setObject(9, LocalDateTime.now(DEFAULT_ZONE_ID));
            psm.setObject(10, task.getTaskId().getName());
            psm.setObject(11, task.getTaskId().getGroup());
            psm.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new ChaconneException(e.getMessage(), e);
        }
    }

    @Override
    public TaskDetail removeTask(TaskId taskId) throws ChaconneException {
        TaskDetail taskDetail = getTaskDetail(taskId);
        if (taskDetail != null) {
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement psm = connection.prepareStatement(SQL_DELETE_STATEMENT)) {
                psm.setObject(1, taskId.getName());
                psm.setObject(2, taskId.getGroup());
                psm.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new ChaconneException(e.getMessage(), e);
            }
        }
        return taskDetail;
    }

    @Override
    public TaskDetail getTaskDetail(TaskId taskId) throws ChaconneException {
        Map<String, Object> record = getTaskDetailRecord(taskId);
        return new JdbcTaskDetail(record);
    }

    private Map<String, Object> getTaskDetailRecord(TaskId taskId) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm = connection.prepareStatement(SQL_SELECT_ONE_STATEMENT)) {
            psm.setObject(1, taskId.getName());
            psm.setObject(2, taskId.getGroup());
            try (ResultSet rs = psm.executeQuery()) {
                if (rs.next()) {
                    return toMap(rs);
                }
            }
        } catch (SQLException e) {
            throw new ChaconneException(e.getMessage(), e);
        }
        throw new ChaconneException("No task detail by: " + taskId.toString());
    }

    private static Map<String, Object> toMap(ResultSet rs) throws ChaconneException {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            CamelCasedLinkedHashMap info = new CamelCasedLinkedHashMap(columnCount);
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                String columnLabel = rsmd.getColumnLabel(columnIndex);
                int columnType = rsmd.getColumnType(columnIndex);
                Object value = columnType == Types.BLOB ? rs.getBytes(columnIndex)
                        : rs.getObject(columnIndex);
                info.put(columnLabel, value);
            }
            return info;
        } catch (SQLException e) {
            throw new ChaconneException(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasTask(TaskId taskId) throws ChaconneException {
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
            throw new ChaconneException(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public int getTaskCount(String group, String name) throws ChaconneException {
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
            throw new ChaconneException(e.getMessage(), e);
        }
        return 0;
    }


    @Override
    public List<TaskDetailVo> findTaskDetails(String group, String name, int limit, int offset)
            throws ChaconneException {
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
        sql.append(" order by last_modified desc");
        if (limit > 0) {
            sql.append(" limit ?");
            args.add(limit);
        }
        if (offset > 0) {
            sql.append(" offset ?");
            args.add(offset);
        }
        List<TaskDetailVo> voList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                psm.setObject(i + 1, arg);
            }
            try (ResultSet rs = psm.executeQuery()) {
                while (rs.next()) {
                    TaskDetailVo vo = new TaskDetailVo(toMap(rs));
                    voList.add(vo);
                }
            }
        } catch (SQLException e) {
            throw new ChaconneException(e.getMessage(), e);
        }
        return voList;
    }

    @Override
    public List<LocalDateTime> findNextFiredDateTimes(TaskId taskId, LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws ChaconneException {
        Map<String, Object> record = getTaskDetailRecord(taskId);
        CronExpression cronExpression =
                CronExpression.deserialize((byte[]) record.get("cronExpression"));
        List<LocalDateTime> results = cronExpression.list(startDateTime, endDateTime);
        return results;
    }

    @Override
    public List<TaskId> findUpcomingTasksBetween(LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws ChaconneException {
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
            throw new ChaconneException(e.getMessage(), e);
        }
        return list;
    }

    @Override
    public LocalDateTime computeNextFiredDateTime(TaskId taskId,
            LocalDateTime previousFiredDateTime) throws ChaconneException {
        Map<String, Object> record = getTaskDetailRecord(taskId);
        LocalDateTime prevFiredDateTime = (LocalDateTime) record.get("nextFiredDateTime");
        CronExpression cronExpression =
                CronExpression.deserialize((byte[]) record.get("cronExpression"));
        LocalDateTime nextFiredDateTime = cronExpression.getNextFiredDateTime(prevFiredDateTime);
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm =
                        connection.prepareStatement(SQL_SET_TASK_NEXT_FIRED_STATEMENT)) {
            psm.setObject(1, nextFiredDateTime);
            psm.setObject(2, prevFiredDateTime);
            psm.setBytes(3, cronExpression.serialize());
            psm.setObject(4, LocalDateTime.now(DEFAULT_ZONE_ID));
            psm.setObject(5, taskId.getName());
            psm.setObject(6, taskId.getGroup());
            psm.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new ChaconneException(e.getMessage(), e);
        }
        return nextFiredDateTime;
    }

    @Override
    public void setTaskStatus(TaskId taskId, TaskStatus status) throws ChaconneException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement psm =
                        connection.prepareStatement(SQL_SET_TASK_STATUS_STATEMENT)) {
            psm.setObject(1, status.name().toUpperCase());
            psm.setObject(2, LocalDateTime.now(DEFAULT_ZONE_ID));
            psm.setObject(3, taskId.getName());
            psm.setObject(4, taskId.getGroup());
            psm.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new ChaconneException(e.getMessage(), e);
        }
    }


}
