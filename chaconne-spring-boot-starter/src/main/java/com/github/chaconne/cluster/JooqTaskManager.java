package com.github.chaconne.cluster;

import static com.github.chaconne.Settings.DEFAULT_ZONE_ID;
import static com.github.chaconne.jooq.tables.CronTaskDetail.CRON_TASK_DETAIL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.tools.LoggerListener;
import com.github.chaconne.ChaconneException;
import com.github.chaconne.CustomTask;
import com.github.chaconne.CustomTaskFactory;
import com.github.chaconne.DefaultCustomTaskFactory;
import com.github.chaconne.Task;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskId;
import com.github.chaconne.TaskManager;
import com.github.chaconne.TaskStatus;
import com.github.chaconne.jooq.tables.records.CronTaskDetailRecord;
import com.github.chaconne.utils.CamelCasedLinkedHashMap;
import com.github.cronsmith.cron.CronExpression;

/**
 * 
 * @Description: JooqTaskManager
 * @Author: Fred Feng
 * @Date: 10/04/2025
 * @Version 1.0.0
 */
public class JooqTaskManager implements TaskManager {

    private final DSLContext dsl;

    public JooqTaskManager(DataSource ds, SQLDialect sqlDialect) {
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();
        defaultConfiguration.setDataSource(ds);
        defaultConfiguration.setSQLDialect(sqlDialect);
        defaultConfiguration.set(new LoggerListener());
        this.dsl = DSL.using(defaultConfiguration);
    }

    public JooqTaskManager(DSLContext dsl) {
        this.dsl = dsl;
    }

    private CustomTaskFactory customTaskFactory = new DefaultCustomTaskFactory();

    public void setCustomTaskFactory(CustomTaskFactory customTaskFactory) {
        this.customTaskFactory = customTaskFactory;
    }

    private class JooqTaskDetail implements TaskDetail {

        private final CronTaskDetailRecord cronTaskDetailRecord;

        JooqTaskDetail(CronTaskDetailRecord cronTaskDetailRecord) {
            this.cronTaskDetailRecord = cronTaskDetailRecord;
            this.task = customTaskFactory
                    .createTaskObject(new CamelCasedLinkedHashMap(cronTaskDetailRecord.intoMap()));
        }

        private final Task task;

        @Override
        public Task getTask() {
            return task;
        }

        @Override
        public String getInitialParameter() {
            return cronTaskDetailRecord.getInitialParameter();
        }

        @Override
        public TaskStatus getTaskStatus() {
            return TaskStatus.valueOf(cronTaskDetailRecord.getTaskStatus().toUpperCase());
        }

        @Override
        public LocalDateTime getNextFiredDateTime() {
            return cronTaskDetailRecord.getNextFiredDatetime();
        }

        @Override
        public LocalDateTime getPreviousFiredDateTime() {
            return cronTaskDetailRecord.getPrevFiredDatetime();
        }

        @Override
        public LocalDateTime getLastModified() {
            return cronTaskDetailRecord.getLastModified();
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append("Task Group: ").append(cronTaskDetailRecord.getTaskGroup())
                    .append(", Task Name").append(cronTaskDetailRecord.getTaskName())
                    .append(", Task Class").append(cronTaskDetailRecord.getTaskClass())
                    .append(", Task Method").append(cronTaskDetailRecord.getTaskMethod())
                    .append(", Task Status: ").append(cronTaskDetailRecord.getTaskStatus())
                    .append(", Previous Fired: ")
                    .append(cronTaskDetailRecord.getPrevFiredDatetime()).append(", Next Fired: ")
                    .append(cronTaskDetailRecord.getNextFiredDatetime());
            return str.toString();
        }

    }

    @Override
    public TaskDetail saveTask(Task task, String initialParameter) throws ChaconneException {
        String taskClassName = task.getClass().getName();
        String taskMethodName = Task.DEFAULT_METHOD_NAME;
        String url = null;
        if (task instanceof CustomTask) {
            taskClassName = ((CustomTask) task).getTaskClassName();
            taskMethodName = ((CustomTask) task).getTaskMethodName();
            url = ((CustomTask) task).getUrl();
        }
        byte[] data = task.getCronExpression().serialize();
        String cron = task.getCronExpression().toString();
        if (hasTask(task.getTaskId())) {
            dsl.update(CRON_TASK_DETAIL).set(CRON_TASK_DETAIL.TASK_CLASS, taskClassName)
                    .set(CRON_TASK_DETAIL.TASK_METHOD, taskMethodName)
                    .set(CRON_TASK_DETAIL.URL, url)
                    .set(CRON_TASK_DETAIL.DESCRIPTION, task.getDescription())
                    .set(CRON_TASK_DETAIL.CRON_EXPRESSION, data).set(CRON_TASK_DETAIL.CRON, cron)
                    .set(CRON_TASK_DETAIL.MAX_RETRY_COUNT, task.getMaxRetryCount())
                    .set(CRON_TASK_DETAIL.TIMEOUT, task.getTimeout())
                    .set(CRON_TASK_DETAIL.LAST_MODIFIED, LocalDateTime.now(DEFAULT_ZONE_ID))
                    .set(CRON_TASK_DETAIL.INITIAL_PARAMETER,
                            StringUtils.isNotBlank(initialParameter) ? initialParameter
                                    : task.getInitialParameter())
                    .set(CRON_TASK_DETAIL.TASK_STATUS, TaskStatus.STANDBY.name().toUpperCase())
                    .where(CRON_TASK_DETAIL.TASK_NAME.eq(task.getTaskId().getName())
                            .and(CRON_TASK_DETAIL.TASK_GROUP.eq(task.getTaskId().getGroup())))
                    .execute();
        } else {
            dsl.insertInto(CRON_TASK_DETAIL)
                    .columns(CRON_TASK_DETAIL.TASK_NAME, CRON_TASK_DETAIL.TASK_GROUP,
                            CRON_TASK_DETAIL.TASK_CLASS, CRON_TASK_DETAIL.TASK_METHOD,
                            CRON_TASK_DETAIL.URL, CRON_TASK_DETAIL.DESCRIPTION,
                            CRON_TASK_DETAIL.TASK_STATUS, CRON_TASK_DETAIL.CRON_EXPRESSION,
                            CRON_TASK_DETAIL.CRON, CRON_TASK_DETAIL.MAX_RETRY_COUNT,
                            CRON_TASK_DETAIL.TIMEOUT, CRON_TASK_DETAIL.LAST_MODIFIED,
                            CRON_TASK_DETAIL.INITIAL_PARAMETER)
                    .values(task.getTaskId().getName(), task.getTaskId().getGroup(), taskClassName,
                            taskMethodName, url, task.getDescription(),
                            TaskStatus.STANDBY.name().toUpperCase(), data, cron,
                            task.getMaxRetryCount(), task.getTimeout(),
                            LocalDateTime.now(DEFAULT_ZONE_ID),
                            StringUtils.isNotBlank(initialParameter) ? initialParameter
                                    : task.getInitialParameter())
                    .execute();
        }
        return getTaskDetail(task.getTaskId());
    }

    @Override
    public TaskDetail removeTask(TaskId taskId) throws ChaconneException {
        TaskDetail taskDetail = getTaskDetail(taskId);
        if (taskDetail != null) {
            dsl.deleteFrom(CRON_TASK_DETAIL).where(CRON_TASK_DETAIL.TASK_NAME.eq(taskId.getName())
                    .and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskId.getGroup()))).execute();
        }
        return taskDetail;
    }

    @Override
    public TaskDetail getTaskDetail(TaskId taskId) throws ChaconneException {
        CronTaskDetailRecord data = dsl.selectFrom(CRON_TASK_DETAIL)
                .where(CRON_TASK_DETAIL.TASK_NAME.eq(taskId.getName())
                        .and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskId.getGroup())))
                .fetchOne();
        return new JooqTaskDetail(data);
    }

    @Override
    public boolean hasTask(TaskId taskId) throws ChaconneException {
        Integer result = dsl.selectCount().from(CRON_TASK_DETAIL)
                .where(CRON_TASK_DETAIL.TASK_NAME.eq(taskId.getName())
                        .and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskId.getGroup())))
                .fetchOne(0, Integer.class);
        return result != null && result.intValue() > 0;
    }

    @Override
    public int getTaskCount(String taskGroup, String taskName, String taskClass)
            throws ChaconneException {
        SelectConditionStep<Record1<Integer>> conditionStep =
                dsl.selectCount().from(CRON_TASK_DETAIL).where(DSL.trueCondition());
        if (StringUtils.isNotBlank(taskGroup)) {
            conditionStep = conditionStep.and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskGroup));
        }
        if (StringUtils.isNotBlank(taskName)) {
            conditionStep =
                    conditionStep.and(CRON_TASK_DETAIL.TASK_NAME.like("%" + taskName + "%"));
        }
        if (StringUtils.isNotBlank(taskClass)) {
            conditionStep =
                    conditionStep.and(CRON_TASK_DETAIL.TASK_CLASS.like("%" + taskClass + "%"));
        }
        Integer result = conditionStep.fetchOne(0, Integer.class);
        return result != null ? result.intValue() : 0;
    }

    @Override
    public List<TaskDetail> findTaskDetails(String taskGroup, String taskName, String taskClass,
            int limit, int offset) throws ChaconneException {
        SelectConditionStep<CronTaskDetailRecord> conditionStep =
                dsl.selectFrom(CRON_TASK_DETAIL).where(DSL.trueCondition());
        if (StringUtils.isNotBlank(taskGroup)) {
            conditionStep = conditionStep.and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskGroup));
        }
        if (StringUtils.isNotBlank(taskName)) {
            conditionStep =
                    conditionStep.and(CRON_TASK_DETAIL.TASK_NAME.like("%" + taskName + "%"));
        }
        if (StringUtils.isNotBlank(taskClass)) {
            conditionStep =
                    conditionStep.and(CRON_TASK_DETAIL.TASK_CLASS.like("%" + taskClass + "%"));
        }
        Result<CronTaskDetailRecord> records = conditionStep
                .orderBy(CRON_TASK_DETAIL.LAST_MODIFIED.desc()).limit(limit).offset(offset).fetch();
        if (records != null && records.size() > 0) {
            List<TaskDetail> results = new ArrayList<TaskDetail>();
            for (CronTaskDetailRecord r : records) {
                results.add(new JooqTaskDetail(r));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<LocalDateTime> findNextFiredDateTimes(TaskId taskId, LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws ChaconneException {
        CronTaskDetailRecord data = dsl.selectFrom(CRON_TASK_DETAIL)
                .where(CRON_TASK_DETAIL.TASK_NAME.eq(taskId.getName())
                        .and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskId.getGroup())))
                .fetchOne();
        CronExpression cronExpression = CronExpression.deserialize(data.getCronExpression());
        return cronExpression.list(startDateTime, endDateTime);
    }

    @Override
    public List<TaskId> findUpcomingTasksBetween(LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws ChaconneException {
        System.out.println("findUpcomingTasksBetween: " + startDateTime + ", " + endDateTime);
        Result<Record2<String, String>> records = dsl
                .select(CRON_TASK_DETAIL.TASK_GROUP, CRON_TASK_DETAIL.TASK_NAME)
                .from(CRON_TASK_DETAIL)
                .where(CRON_TASK_DETAIL.NEXT_FIRED_DATETIME.between(startDateTime, endDateTime))
                .fetch();
        if (records != null && records.size() > 0) {
            return records.stream().map(r -> TaskId.of(r.value1(), r.value2())).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public LocalDateTime computeNextFiredDateTime(TaskId taskId,
            LocalDateTime previousFiredDateTime) throws ChaconneException {
        CronTaskDetailRecord data = dsl.selectFrom(CRON_TASK_DETAIL)
                .where(CRON_TASK_DETAIL.TASK_NAME.eq(taskId.getName())
                        .and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskId.getGroup())))
                .fetchOne();
        if (data != null) {
            CronExpression cronExpression = CronExpression.deserialize(data.getCronExpression());
            LocalDateTime nextFired =
                    cronExpression.getNextFiredDateTime(data.getNextFiredDatetime());
            if (nextFired != null) {
                data.setPrevFiredDatetime(data.getNextFiredDatetime());
                data.setNextFiredDatetime(nextFired);
                data.setCronExpression(cronExpression.serialize());
                dsl.update(CRON_TASK_DETAIL).set(data)
                        .where(CRON_TASK_DETAIL.TASK_NAME.eq(taskId.getName())
                                .and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskId.getGroup())))
                        .execute();
                return nextFired;
            }
        }
        return null;
    }

    @Override
    public void setTaskStatus(TaskId taskId, TaskStatus status) throws ChaconneException {
        dsl.update(CRON_TASK_DETAIL).set(CRON_TASK_DETAIL.TASK_STATUS, (String) status.getValue())
                .set(CRON_TASK_DETAIL.LAST_MODIFIED, LocalDateTime.now(DEFAULT_ZONE_ID))
                .where(CRON_TASK_DETAIL.TASK_NAME.eq(taskId.getName())
                        .and(CRON_TASK_DETAIL.TASK_GROUP.eq(taskId.getGroup())))
                .execute();
    }

}
