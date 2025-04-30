package com.github.chaconne.client;

/**
 * 
 * @Description: CreateSimpleTaskRequest
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
public class CreateSimpleTaskRequest extends TaskIdRequest {

    private static final long serialVersionUID = 164861739981698390L;
    private String taskUrl;
    private String taskMethod;
    private String dataType;
    private String data;
    private String description;
    private String cronExpression;
    private int maxRetryCount;
    private long timeout;
    private UpdatePolicy updatePolicy = UpdatePolicy.CREATE;

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public UpdatePolicy getUpdatePolicy() {
        return updatePolicy;
    }

    public void setUpdatePolicy(UpdatePolicy updatePolicy) {
        this.updatePolicy = updatePolicy;
    }

}
