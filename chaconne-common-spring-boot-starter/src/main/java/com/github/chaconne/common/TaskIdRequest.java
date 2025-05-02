package com.github.chaconne.common;

import java.io.Serializable;

/**
 * 
 * @Description: TaskIdRequest
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public class TaskIdRequest implements Serializable {

    private static final long serialVersionUID = -3558585731651860011L;
    private String taskName;
    private String taskGroup;
    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
