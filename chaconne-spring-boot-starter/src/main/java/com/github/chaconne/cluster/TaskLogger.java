package com.github.chaconne.cluster;

import static com.github.chaconne.jooq.tables.CronTaskDetail.CRON_TASK_DETAIL;
import static com.github.chaconne.jooq.tables.CronTaskLog.CRON_TASK_LOG;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.jooq.DSLContext;
import com.github.chaconne.CustomTask;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskListener;
import com.github.chaconne.common.utils.ExceptionUtils;

/**
 * 
 * @Description: TaskLogger
 * @Author: Fred Feng
 * @Date: 25/04/2025
 * @Version 1.0.0
 */
public class TaskLogger implements TaskListener {

    private final DSLContext dsl;

    public TaskLogger(DSLContext dslContext) {
        this.dsl = dslContext;
    }

    @Override
    public void onTaskScheduled(ZonedDateTime scheduledDateTime, TaskDetail taskDetail) {
        CustomTask task = (CustomTask) taskDetail.getTask();
        dsl.insertInto(CRON_TASK_LOG)
                .columns(CRON_TASK_LOG.TASK_NAME, CRON_TASK_LOG.TASK_GROUP,
                        CRON_TASK_LOG.TASK_CLASS, CRON_TASK_LOG.TASK_METHOD, CRON_TASK_LOG.URL,
                        CRON_TASK_LOG.INITIAL_PARAMETER, CRON_TASK_LOG.SCHEDULED_DATETIME)
                .values(task.getTaskId().getName(), task.getTaskId().getGroup(),
                        task.getTaskClassName(), task.getTaskMethodName(), task.getUrl(),
                        taskDetail.getInitialParameter(), scheduledDateTime.toLocalDateTime())
                .execute();
    }

    @Override
    public void onTaskBegan(ZonedDateTime firedDateTime, TaskDetail taskDetail) {
        CustomTask task = (CustomTask) taskDetail.getTask();
        dsl.update(CRON_TASK_LOG).set(CRON_TASK_LOG.FIRED_DATETIME, firedDateTime.toLocalDateTime())
                .where(CRON_TASK_DETAIL.TASK_NAME.eq(task.getTaskId().getName())
                        .and(CRON_TASK_DETAIL.TASK_GROUP.eq(task.getTaskId().getGroup())))
                .execute();
    }

    @Override
    public void onTaskEnded(ZonedDateTime firedDateTime, TaskDetail taskDetail, Object returnValue,
            Throwable e) {
        ZonedDateTime now = ZonedDateTime.now(firedDateTime.getZone());
        long elapsed = Duration.between(firedDateTime, now).getSeconds();
        CustomTask task = (CustomTask) taskDetail.getTask();
        dsl.update(CRON_TASK_LOG).set(CRON_TASK_LOG.STATUS, e != null ? 1 : 0)
                .set(CRON_TASK_LOG.ELAPSED, elapsed)
                .set(CRON_TASK_LOG.RETURN_VALUE,
                        returnValue != null ? returnValue.toString() : null)
                .set(CRON_TASK_LOG.ERROR_DETAIL, e != null ? ExceptionUtils.toString(e) : null)
                .where(CRON_TASK_DETAIL.TASK_NAME.eq(task.getTaskId().getName())
                        .and(CRON_TASK_DETAIL.TASK_GROUP.eq(task.getTaskId().getGroup())))
                .execute();
    }



}
