package com.github.chaconne.cluster;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.github.chaconne.CustomTask;
import com.github.chaconne.Task;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskStatus;
import com.github.chaconne.common.utils.BeanUtils;
import com.github.chaconne.utils.CamelCasedLinkedHashMap;

/**
 * 
 * @Description: TaskInfoVo
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public class TaskDetailVo implements Serializable {

    private static final long serialVersionUID = 4048937770243324032L;
    private String taskName;
    private String taskGroup;
    private String taskClass;
    private String taskMethod;
    private String description;
    private String cron;
    private LocalDateTime prevFiredDatetime;
    private LocalDateTime nextFiredDatetime;
    private LocalDateTime lastModified;
    private int maxRetryCount;
    private long timeout;
    private TaskStatus taskStatus;

    public TaskDetailVo() {}

    public TaskDetailVo(Map<String, Object> map) {
        Map<String, Object> copy =
                map instanceof CamelCasedLinkedHashMap ? (CamelCasedLinkedHashMap) map
                        : new CamelCasedLinkedHashMap(map);
        BeanUtils.populateBean(this, copy, true);
    }

    public TaskDetailVo(TaskDetail taskDetail) {
        this.taskName = taskDetail.getTask().getTaskId().getName();
        this.taskGroup = taskDetail.getTask().getTaskId().getGroup();
        this.taskClass = taskDetail.getTask() instanceof CustomTask
                ? ((CustomTask) taskDetail.getTask()).getTaskClassName()
                : taskDetail.getTask().getClass().getName();
        this.taskMethod = taskDetail.getTask() instanceof CustomTask
                ? ((CustomTask) taskDetail.getTask()).getTaskMethodName()
                : Task.DEFAULT_METHOD_NAME;
        this.description = taskDetail.getTask().getDescription();
        this.description = taskDetail.getTask().getCronExpression().toString();
        this.maxRetryCount = taskDetail.getTask().getMaxRetryCount();
        this.timeout = taskDetail.getTask().getTimeout();
        this.prevFiredDatetime = taskDetail.getPreviousFiredDateTime();
        this.nextFiredDatetime = taskDetail.getNextFiredDateTime();
        this.lastModified = taskDetail.getLastModified();
        this.taskStatus = taskDetail.getTaskStatus();
    }

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

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }

    public LocalDateTime getPrevFiredDatetime() {
        return prevFiredDatetime;
    }

    public void setPrevFiredDatetime(LocalDateTime prevFiredDatetime) {
        this.prevFiredDatetime = prevFiredDatetime;
    }

    public LocalDateTime getNextFiredDatetime() {
        return nextFiredDatetime;
    }

    public void setNextFiredDatetime(LocalDateTime nextFiredDatetime) {
        this.nextFiredDatetime = nextFiredDatetime;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
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

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
