/*
 * This file is generated by jOOQ.
 */
package com.github.chaconne.jooq.tables.records;


import com.github.chaconne.jooq.tables.CronTaskLog;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class CronTaskLogRecord extends TableRecordImpl<CronTaskLogRecord> implements Record12<String, String, String, String, String, String, LocalDateTime, LocalDateTime, String, Long, Integer, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>cron_task_log.task_name</code>.
     */
    public CronTaskLogRecord setTaskName(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.task_name</code>.
     */
    public String getTaskName() {
        return (String) get(0);
    }

    /**
     * Setter for <code>cron_task_log.task_group</code>.
     */
    public CronTaskLogRecord setTaskGroup(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.task_group</code>.
     */
    public String getTaskGroup() {
        return (String) get(1);
    }

    /**
     * Setter for <code>cron_task_log.task_class</code>.
     */
    public CronTaskLogRecord setTaskClass(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.task_class</code>.
     */
    public String getTaskClass() {
        return (String) get(2);
    }

    /**
     * Setter for <code>cron_task_log.task_method</code>.
     */
    public CronTaskLogRecord setTaskMethod(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.task_method</code>.
     */
    public String getTaskMethod() {
        return (String) get(3);
    }

    /**
     * Setter for <code>cron_task_log.url</code>.
     */
    public CronTaskLogRecord setUrl(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.url</code>.
     */
    public String getUrl() {
        return (String) get(4);
    }

    /**
     * Setter for <code>cron_task_log.initial_parameter</code>.
     */
    public CronTaskLogRecord setInitialParameter(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.initial_parameter</code>.
     */
    public String getInitialParameter() {
        return (String) get(5);
    }

    /**
     * Setter for <code>cron_task_log.scheduled_datetime</code>.
     */
    public CronTaskLogRecord setScheduledDatetime(LocalDateTime value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.scheduled_datetime</code>.
     */
    public LocalDateTime getScheduledDatetime() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>cron_task_log.fired_datetime</code>.
     */
    public CronTaskLogRecord setFiredDatetime(LocalDateTime value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.fired_datetime</code>.
     */
    public LocalDateTime getFiredDatetime() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>cron_task_log.return_value</code>.
     */
    public CronTaskLogRecord setReturnValue(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.return_value</code>.
     */
    public String getReturnValue() {
        return (String) get(8);
    }

    /**
     * Setter for <code>cron_task_log.elapsed</code>.
     */
    public CronTaskLogRecord setElapsed(Long value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.elapsed</code>.
     */
    public Long getElapsed() {
        return (Long) get(9);
    }

    /**
     * Setter for <code>cron_task_log.status</code>.
     */
    public CronTaskLogRecord setStatus(Integer value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.status</code>.
     */
    public Integer getStatus() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>cron_task_log.error_detail</code>.
     */
    public CronTaskLogRecord setErrorDetail(String value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>cron_task_log.error_detail</code>.
     */
    public String getErrorDetail() {
        return (String) get(11);
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row12<String, String, String, String, String, String, LocalDateTime, LocalDateTime, String, Long, Integer, String> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    @Override
    public Row12<String, String, String, String, String, String, LocalDateTime, LocalDateTime, String, Long, Integer, String> valuesRow() {
        return (Row12) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return CronTaskLog.CRON_TASK_LOG.TASK_NAME;
    }

    @Override
    public Field<String> field2() {
        return CronTaskLog.CRON_TASK_LOG.TASK_GROUP;
    }

    @Override
    public Field<String> field3() {
        return CronTaskLog.CRON_TASK_LOG.TASK_CLASS;
    }

    @Override
    public Field<String> field4() {
        return CronTaskLog.CRON_TASK_LOG.TASK_METHOD;
    }

    @Override
    public Field<String> field5() {
        return CronTaskLog.CRON_TASK_LOG.URL;
    }

    @Override
    public Field<String> field6() {
        return CronTaskLog.CRON_TASK_LOG.INITIAL_PARAMETER;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return CronTaskLog.CRON_TASK_LOG.SCHEDULED_DATETIME;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return CronTaskLog.CRON_TASK_LOG.FIRED_DATETIME;
    }

    @Override
    public Field<String> field9() {
        return CronTaskLog.CRON_TASK_LOG.RETURN_VALUE;
    }

    @Override
    public Field<Long> field10() {
        return CronTaskLog.CRON_TASK_LOG.ELAPSED;
    }

    @Override
    public Field<Integer> field11() {
        return CronTaskLog.CRON_TASK_LOG.STATUS;
    }

    @Override
    public Field<String> field12() {
        return CronTaskLog.CRON_TASK_LOG.ERROR_DETAIL;
    }

    @Override
    public String component1() {
        return getTaskName();
    }

    @Override
    public String component2() {
        return getTaskGroup();
    }

    @Override
    public String component3() {
        return getTaskClass();
    }

    @Override
    public String component4() {
        return getTaskMethod();
    }

    @Override
    public String component5() {
        return getUrl();
    }

    @Override
    public String component6() {
        return getInitialParameter();
    }

    @Override
    public LocalDateTime component7() {
        return getScheduledDatetime();
    }

    @Override
    public LocalDateTime component8() {
        return getFiredDatetime();
    }

    @Override
    public String component9() {
        return getReturnValue();
    }

    @Override
    public Long component10() {
        return getElapsed();
    }

    @Override
    public Integer component11() {
        return getStatus();
    }

    @Override
    public String component12() {
        return getErrorDetail();
    }

    @Override
    public String value1() {
        return getTaskName();
    }

    @Override
    public String value2() {
        return getTaskGroup();
    }

    @Override
    public String value3() {
        return getTaskClass();
    }

    @Override
    public String value4() {
        return getTaskMethod();
    }

    @Override
    public String value5() {
        return getUrl();
    }

    @Override
    public String value6() {
        return getInitialParameter();
    }

    @Override
    public LocalDateTime value7() {
        return getScheduledDatetime();
    }

    @Override
    public LocalDateTime value8() {
        return getFiredDatetime();
    }

    @Override
    public String value9() {
        return getReturnValue();
    }

    @Override
    public Long value10() {
        return getElapsed();
    }

    @Override
    public Integer value11() {
        return getStatus();
    }

    @Override
    public String value12() {
        return getErrorDetail();
    }

    @Override
    public CronTaskLogRecord value1(String value) {
        setTaskName(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value2(String value) {
        setTaskGroup(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value3(String value) {
        setTaskClass(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value4(String value) {
        setTaskMethod(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value5(String value) {
        setUrl(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value6(String value) {
        setInitialParameter(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value7(LocalDateTime value) {
        setScheduledDatetime(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value8(LocalDateTime value) {
        setFiredDatetime(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value9(String value) {
        setReturnValue(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value10(Long value) {
        setElapsed(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value11(Integer value) {
        setStatus(value);
        return this;
    }

    @Override
    public CronTaskLogRecord value12(String value) {
        setErrorDetail(value);
        return this;
    }

    @Override
    public CronTaskLogRecord values(String value1, String value2, String value3, String value4, String value5, String value6, LocalDateTime value7, LocalDateTime value8, String value9, Long value10, Integer value11, String value12) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CronTaskLogRecord
     */
    public CronTaskLogRecord() {
        super(CronTaskLog.CRON_TASK_LOG);
    }

    /**
     * Create a detached, initialised CronTaskLogRecord
     */
    public CronTaskLogRecord(String taskName, String taskGroup, String taskClass, String taskMethod, String url, String initialParameter, LocalDateTime scheduledDatetime, LocalDateTime firedDatetime, String returnValue, Long elapsed, Integer status, String errorDetail) {
        super(CronTaskLog.CRON_TASK_LOG);

        setTaskName(taskName);
        setTaskGroup(taskGroup);
        setTaskClass(taskClass);
        setTaskMethod(taskMethod);
        setUrl(url);
        setInitialParameter(initialParameter);
        setScheduledDatetime(scheduledDatetime);
        setFiredDatetime(firedDatetime);
        setReturnValue(returnValue);
        setElapsed(elapsed);
        setStatus(status);
        setErrorDetail(errorDetail);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised CronTaskLogRecord
     */
    public CronTaskLogRecord(com.github.chaconne.jooq.tables.pojos.CronTaskLog value) {
        super(CronTaskLog.CRON_TASK_LOG);

        if (value != null) {
            setTaskName(value.getTaskName());
            setTaskGroup(value.getTaskGroup());
            setTaskClass(value.getTaskClass());
            setTaskMethod(value.getTaskMethod());
            setUrl(value.getUrl());
            setInitialParameter(value.getInitialParameter());
            setScheduledDatetime(value.getScheduledDatetime());
            setFiredDatetime(value.getFiredDatetime());
            setReturnValue(value.getReturnValue());
            setElapsed(value.getElapsed());
            setStatus(value.getStatus());
            setErrorDetail(value.getErrorDetail());
            resetChangedOnNotNull();
        }
    }
}
