package com.github.chaconne.client;

import java.io.Serializable;

/**
 * 
 * @Description: CreateTaskRequest
 * @Author: Fred Feng
 * @Date: 11/04/2025
 * @Version 1.0.0
 */
public class CreateTaskRequest implements TaskIdRequest, Serializable {

    private static final long serialVersionUID = 1976938346500751980L;

    private String taskName;
    private String taskGroup;
    private String taskClass;
    private String taskMethod;
    private String description;
    private String cron;
    private int maxRetryCount;
    private long timeout;
    private String initialParameter;
    private UpdatePolicy updatePolicy = UpdatePolicy.CREATE;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
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

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
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

    public UpdatePolicy getUpdatePolicy() {
        return updatePolicy;
    }

    public void setUpdatePolicy(UpdatePolicy updatePolicy) {
        this.updatePolicy = updatePolicy;
    }

}
