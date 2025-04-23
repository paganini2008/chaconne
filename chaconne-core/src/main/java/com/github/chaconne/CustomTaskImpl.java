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
 * @Description: CustomTaskImpl
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public class CustomTaskImpl implements CustomTask {

    private static final Logger log = LoggerFactory.getLogger(CustomTaskImpl.class);

    public CustomTaskImpl(Map<String, Object> info, TaskInvocation taskInvocation) {
        this.info = info;
        this.taskInvocation = taskInvocation;
    }

    private final Map<String, Object> info;
    private final TaskInvocation taskInvocation;

    @Override
    public TaskId getTaskId() {
        return TaskId.of((String) info.getOrDefault("taskGroup", TaskId.DEFAULT_GROUP),
                (String) info.get("taskName"));
    }

    @Override
    public String getTaskClassName() {
        return (String) info.get("taskClass");
    }

    @Override
    public String getTaskMethodName() {
        return (String) info.get("taskMethod");
    }

    @Override
    public String getDescription() {
        return (String) info.get("description");
    }

    @Override
    public long getTimeout() {
        return (Long) info.getOrDefault("timeout", -1L);
    }

    @Override
    public int getMaxRetryCount() {
        return (Integer) info.getOrDefault("maxRetryCount", -1);
    }

    @Override
    public CronExpression getCronExpression() {
        Object object = info.get("cronExpression");
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
        String taskClassName = (String) info.get("taskClass");
        String taskMethodName = (String) info.getOrDefault("taskMethod", DEFAULT_METHOD_NAME);
        try {
            return taskInvocation.invokeTaskMethod(getTaskId(), taskClassName, taskMethodName,
                    initialParameter);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TaskId: " + getTaskId() + ", TaskClass: " + getTaskClassName() + ", TaskMethod: "
                + getTaskMethodName();
    }

}
