package com.github.chaconne;

import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chaconne.utils.HttpClientUtils;
import com.github.cronsmith.CRON;
import com.github.cronsmith.cron.CronExpression;

/**
 * 
 * @Description: SimpleTask
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
public class SimpleTask implements Task {

    private String taskGroup;
    private String taskName;
    private String url;
    private String httpMethod;
    private Map<String, String> httpHeaders;
    private String dataType;
    private String data;
    private String description;
    private String cronExpression;
    private int maxRetryCount;
    private long timeout;

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
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

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public TaskId getTaskId() {
        return TaskId.of(taskGroup, taskName);
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public CronExpression getCronExpression() {
        return CRON.parse(cronExpression);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    /**
     * 
     * @Description: OneTimeRequest
     * @Author: Fred Feng
     * @Date: 30/04/2025
     * @Version 1.0.0
     */
    static class OneTimeRequest {

        String url;
        String httpMethod;
        Map<String, String> httpHeaders;
        String dataType;
        String data;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        public Map<String, String> getHttpHeaders() {
            return httpHeaders;
        }

        public void setHttpHeaders(Map<String, String> httpHeaders) {
            this.httpHeaders = httpHeaders;
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

    }

    @Override
    public String getInitialParameter() {
        OneTimeRequest oneTimeRequest = new OneTimeRequest();
        oneTimeRequest.url = this.url;
        oneTimeRequest.httpMethod = this.httpMethod;
        oneTimeRequest.httpHeaders = this.httpHeaders;
        oneTimeRequest.dataType = this.dataType;
        oneTimeRequest.data = this.data;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(oneTimeRequest);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public Object execute(String initialParameter) {
        ObjectMapper objectMapper = new ObjectMapper();
        OneTimeRequest oneTimeRequest;
        try {
            oneTimeRequest = objectMapper.readValue(initialParameter, OneTimeRequest.class);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        try {
            return HttpClientUtils.sendRequest(oneTimeRequest.getUrl(),
                    oneTimeRequest.getHttpMethod(), oneTimeRequest.getHttpHeaders(),
                    oneTimeRequest.getDataType(), oneTimeRequest.getData());
        } catch (IOException e) {
            throw new TaskInvocationException(e.getMessage(), e);
        }
    }

}
