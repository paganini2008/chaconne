package com.github.chaconne.common;

import java.io.Serializable;

/**
 * 
 * @Description: CreateTaskRequest
 * @Author: Fred Feng
 * @Date: 11/04/2025
 * @Version 1.0.0
 */
public class CreateTaskRequest extends TaskIdRequest implements Serializable {

    private static final long serialVersionUID = 1976938346500751980L;

    private String serviceId;
    private String taskClass;
    private String taskMethod;
    private String description;
    private String cronExpression;
    private int maxRetryCount;
    private long timeout;
    private String initialParameter;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getInitialParameter() {
        return initialParameter;
    }

    public void setInitialParameter(String initialParameter) {
        this.initialParameter = initialParameter;
    }

}
