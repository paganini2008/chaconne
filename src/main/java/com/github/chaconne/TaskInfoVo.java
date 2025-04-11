package com.github.chaconne;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @Description: TaskInfoVo
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public class TaskInfoVo implements Serializable {

    private static final long serialVersionUID = 4048937770243324032L;
    private String taskName;
    private String taskGroup;
    private String taskClass;
    private String description;
    private String cron;
    private LocalDateTime prevFiredDateTime;
    private LocalDateTime nextFiredDateTime;
    private LocalDateTime lastModified;
    private int maxRetryCount;
    private long timeout;
    private TaskStatus taskStatus;

    public TaskInfoVo() {}

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

    public LocalDateTime getPrevFiredDateTime() {
        return prevFiredDateTime;
    }

    public void setPrevFiredDateTime(LocalDateTime prevFiredDateTime) {
        this.prevFiredDateTime = prevFiredDateTime;
    }

    public LocalDateTime getNextFiredDateTime() {
        return nextFiredDateTime;
    }

    public void setNextFiredDateTime(LocalDateTime nextFiredDateTime) {
        this.nextFiredDateTime = nextFiredDateTime;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
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

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = TaskStatus.valueOf(taskStatus);
    }

}
