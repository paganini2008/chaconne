/*
 * This file is generated by jOOQ.
 */
package com.github.chaconne.jooq.tables;


import com.github.chaconne.jooq.DefaultSchema;
import com.github.chaconne.jooq.tables.records.CronTaskLogRecord;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function12;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row12;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class CronTaskLog extends TableImpl<CronTaskLogRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>cron_task_log</code>
     */
    public static final CronTaskLog CRON_TASK_LOG = new CronTaskLog();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CronTaskLogRecord> getRecordType() {
        return CronTaskLogRecord.class;
    }

    /**
     * The column <code>cron_task_log.task_name</code>.
     */
    public final TableField<CronTaskLogRecord, String> TASK_NAME = createField(DSL.name("task_name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>cron_task_log.task_group</code>.
     */
    public final TableField<CronTaskLogRecord, String> TASK_GROUP = createField(DSL.name("task_group"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>cron_task_log.task_class</code>.
     */
    public final TableField<CronTaskLogRecord, String> TASK_CLASS = createField(DSL.name("task_class"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>cron_task_log.task_method</code>.
     */
    public final TableField<CronTaskLogRecord, String> TASK_METHOD = createField(DSL.name("task_method"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>cron_task_log.url</code>.
     */
    public final TableField<CronTaskLogRecord, String> URL = createField(DSL.name("url"), SQLDataType.VARCHAR(1024), this, "");

    /**
     * The column <code>cron_task_log.initial_parameter</code>.
     */
    public final TableField<CronTaskLogRecord, String> INITIAL_PARAMETER = createField(DSL.name("initial_parameter"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>cron_task_log.scheduled_datetime</code>.
     */
    public final TableField<CronTaskLogRecord, LocalDateTime> SCHEDULED_DATETIME = createField(DSL.name("scheduled_datetime"), SQLDataType.LOCALDATETIME(0), this, "");

    /**
     * The column <code>cron_task_log.fired_datetime</code>.
     */
    public final TableField<CronTaskLogRecord, LocalDateTime> FIRED_DATETIME = createField(DSL.name("fired_datetime"), SQLDataType.LOCALDATETIME(0), this, "");

    /**
     * The column <code>cron_task_log.return_value</code>.
     */
    public final TableField<CronTaskLogRecord, String> RETURN_VALUE = createField(DSL.name("return_value"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>cron_task_log.elapsed</code>.
     */
    public final TableField<CronTaskLogRecord, Long> ELAPSED = createField(DSL.name("elapsed"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>cron_task_log.status</code>.
     */
    public final TableField<CronTaskLogRecord, Integer> STATUS = createField(DSL.name("status"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>cron_task_log.error_detail</code>.
     */
    public final TableField<CronTaskLogRecord, String> ERROR_DETAIL = createField(DSL.name("error_detail"), SQLDataType.CLOB, this, "");

    private CronTaskLog(Name alias, Table<CronTaskLogRecord> aliased) {
        this(alias, aliased, null);
    }

    private CronTaskLog(Name alias, Table<CronTaskLogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>cron_task_log</code> table reference
     */
    public CronTaskLog(String alias) {
        this(DSL.name(alias), CRON_TASK_LOG);
    }

    /**
     * Create an aliased <code>cron_task_log</code> table reference
     */
    public CronTaskLog(Name alias) {
        this(alias, CRON_TASK_LOG);
    }

    /**
     * Create a <code>cron_task_log</code> table reference
     */
    public CronTaskLog() {
        this(DSL.name("cron_task_log"), null);
    }

    public <O extends Record> CronTaskLog(Table<O> child, ForeignKey<O, CronTaskLogRecord> key) {
        super(child, key, CRON_TASK_LOG);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public CronTaskLog as(String alias) {
        return new CronTaskLog(DSL.name(alias), this);
    }

    @Override
    public CronTaskLog as(Name alias) {
        return new CronTaskLog(alias, this);
    }

    @Override
    public CronTaskLog as(Table<?> alias) {
        return new CronTaskLog(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public CronTaskLog rename(String name) {
        return new CronTaskLog(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CronTaskLog rename(Name name) {
        return new CronTaskLog(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public CronTaskLog rename(Table<?> name) {
        return new CronTaskLog(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<String, String, String, String, String, String, LocalDateTime, LocalDateTime, String, Long, Integer, String> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function12<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super Long, ? super Integer, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function12<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super Long, ? super Integer, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
