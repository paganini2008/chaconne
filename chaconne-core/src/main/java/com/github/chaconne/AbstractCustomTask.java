package com.github.chaconne;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.cronsmith.CRON;
import com.github.cronsmith.cron.CronExpression;

/**
 * 
 * @Description: AbstractCustomTask
 * @Author: Fred Feng
 * @Date: 25/04/2025
 * @Version 1.0.0
 */
public abstract class AbstractCustomTask implements CustomTask {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public AbstractCustomTask(Map<String, Object> record) {
        this.record = record;
    }

    protected final Map<String, Object> record;

    @Override
    public TaskId getTaskId() {
        return TaskId.of((String) record.getOrDefault("taskGroup", TaskId.DEFAULT_GROUP),
                (String) record.get("taskName"));
    }

    @Override
    public String getTaskClassName() {
        return (String) record.get("taskClass");
    }

    @Override
    public String getTaskMethodName() {
        return (String) record.get("taskMethod");
    }

    @Override
    public String getUrl() {
        return (String) record.get("url");
    }

    @Override
    public String getDescription() {
        return (String) record.get("description");
    }

    @Override
    public long getTimeout() {
        return (Long) record.getOrDefault("timeout", -1L);
    }

    @Override
    public int getMaxRetryCount() {
        return (Integer) record.getOrDefault("maxRetryCount", -1);
    }

    @Override
    public CronExpression getCronExpression() {
        Object object = record.get("cronExpression");
        if (object instanceof CronExpression) {
            return (CronExpression) object;
        } else if (object instanceof CharSequence) {
            return CRON.parse(object.toString());
        } else if (object instanceof byte[]) {
            return CronExpression.deserialize((byte[]) object);
        } else if (object instanceof LocalDateTime) {
            return CRON.atFuture((LocalDateTime) object);
        } else if (object instanceof LocalDate) {
            return CRON.atFuture((LocalDate) object);
        } else if (object instanceof LocalTime) {
            return CRON.setInterval((LocalTime) object);
        }
        throw new ChaconneException("CronExpression is required");
    }

    @Override
    public Object execute(String initialParameter) {
        String taskClassName = (String) record.get("taskClass");
        String taskMethodName = (String) record.getOrDefault("taskMethod", DEFAULT_METHOD_NAME);
        try {
            return invokeTaskMethod(getTaskId(), taskClassName, taskMethodName, initialParameter);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void handleResult(Object result, Throwable reason) {
        String taskClassName = (String) record.get("taskClass");
        Object taskObject = TaskReflectionUtils.getTaskObject(taskClassName);
        if (taskObject instanceof Task) {
            ((Task) taskObject).handleResult(result, reason);
        }
    }

    protected abstract Object invokeTaskMethod(TaskId taskId, String taskClassName,
            String taskMethodName, String initialParameter);

    @Override
    public String toString() {
        return "TaskId: " + getTaskId() + ", TaskClass: " + getTaskClassName() + ", TaskMethod: "
                + getTaskMethodName();
    }

}
