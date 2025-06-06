/*
 * This file is generated by jOOQ.
 */
package com.github.chaconne.jooq.tables;


import com.github.chaconne.jooq.DefaultSchema;
import com.github.chaconne.jooq.tables.records.CronTaskDetailRecord;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function15;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row15;
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
public class CronTaskDetail extends TableImpl<CronTaskDetailRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>cron_task_detail</code>
     */
    public static final CronTaskDetail CRON_TASK_DETAIL = new CronTaskDetail();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CronTaskDetailRecord> getRecordType() {
        return CronTaskDetailRecord.class;
    }

    /**
     * The column <code>cron_task_detail.task_name</code>.
     */
    public final TableField<CronTaskDetailRecord, String> TASK_NAME = createField(DSL.name("task_name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>cron_task_detail.task_group</code>.
     */
    public final TableField<CronTaskDetailRecord, String> TASK_GROUP = createField(DSL.name("task_group"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>cron_task_detail.task_class</code>.
     */
    public final TableField<CronTaskDetailRecord, String> TASK_CLASS = createField(DSL.name("task_class"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>cron_task_detail.task_method</code>.
     */
    public final TableField<CronTaskDetailRecord, String> TASK_METHOD = createField(DSL.name("task_method"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>cron_task_detail.url</code>.
     */
    public final TableField<CronTaskDetailRecord, String> URL = createField(DSL.name("url"), SQLDataType.VARCHAR(1024), this, "");

    /**
     * The column <code>cron_task_detail.initial_parameter</code>.
     */
    public final TableField<CronTaskDetailRecord, String> INITIAL_PARAMETER = createField(DSL.name("initial_parameter"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>cron_task_detail.description</code>.
     */
    public final TableField<CronTaskDetailRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.VARCHAR(1024), this, "");

    /**
     * The column <code>cron_task_detail.cron_expression</code>.
     */
    public final TableField<CronTaskDetailRecord, byte[]> CRON_EXPRESSION = createField(DSL.name("cron_expression"), SQLDataType.BLOB.nullable(false), this, "");

    /**
     * The column <code>cron_task_detail.cron</code>.
     */
    public final TableField<CronTaskDetailRecord, String> CRON = createField(DSL.name("cron"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>cron_task_detail.next_fired_datetime</code>.
     */
    public final TableField<CronTaskDetailRecord, LocalDateTime> NEXT_FIRED_DATETIME = createField(DSL.name("next_fired_datetime"), SQLDataType.LOCALDATETIME(0), this, "");

    /**
     * The column <code>cron_task_detail.prev_fired_datetime</code>.
     */
    public final TableField<CronTaskDetailRecord, LocalDateTime> PREV_FIRED_DATETIME = createField(DSL.name("prev_fired_datetime"), SQLDataType.LOCALDATETIME(0), this, "");

    /**
     * The column <code>cron_task_detail.task_status</code>.
     */
    public final TableField<CronTaskDetailRecord, String> TASK_STATUS = createField(DSL.name("task_status"), SQLDataType.VARCHAR(45).nullable(false), this, "");

    /**
     * The column <code>cron_task_detail.max_retry_count</code>.
     */
    public final TableField<CronTaskDetailRecord, Integer> MAX_RETRY_COUNT = createField(DSL.name("max_retry_count"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>cron_task_detail.timeout</code>.
     */
    public final TableField<CronTaskDetailRecord, Long> TIMEOUT = createField(DSL.name("timeout"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>cron_task_detail.last_modified</code>.
     */
    public final TableField<CronTaskDetailRecord, LocalDateTime> LAST_MODIFIED = createField(DSL.name("last_modified"), SQLDataType.LOCALDATETIME(0).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "");

    private CronTaskDetail(Name alias, Table<CronTaskDetailRecord> aliased) {
        this(alias, aliased, null);
    }

    private CronTaskDetail(Name alias, Table<CronTaskDetailRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>cron_task_detail</code> table reference
     */
    public CronTaskDetail(String alias) {
        this(DSL.name(alias), CRON_TASK_DETAIL);
    }

    /**
     * Create an aliased <code>cron_task_detail</code> table reference
     */
    public CronTaskDetail(Name alias) {
        this(alias, CRON_TASK_DETAIL);
    }

    /**
     * Create a <code>cron_task_detail</code> table reference
     */
    public CronTaskDetail() {
        this(DSL.name("cron_task_detail"), null);
    }

    public <O extends Record> CronTaskDetail(Table<O> child, ForeignKey<O, CronTaskDetailRecord> key) {
        super(child, key, CRON_TASK_DETAIL);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public CronTaskDetail as(String alias) {
        return new CronTaskDetail(DSL.name(alias), this);
    }

    @Override
    public CronTaskDetail as(Name alias) {
        return new CronTaskDetail(alias, this);
    }

    @Override
    public CronTaskDetail as(Table<?> alias) {
        return new CronTaskDetail(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public CronTaskDetail rename(String name) {
        return new CronTaskDetail(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CronTaskDetail rename(Name name) {
        return new CronTaskDetail(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public CronTaskDetail rename(Table<?> name) {
        return new CronTaskDetail(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row15 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row15<String, String, String, String, String, String, String, byte[], String, LocalDateTime, LocalDateTime, String, Integer, Long, LocalDateTime> fieldsRow() {
        return (Row15) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function15<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super byte[], ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super Integer, ? super Long, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function15<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super byte[], ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? super Integer, ? super Long, ? super LocalDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
