package com.github.chaconne.client;

import java.io.Serializable;

/**
 * 
 * @Description: RunTaskRequest
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public class RunTaskRequest implements TaskIdRequest, Serializable {

    private static final long serialVersionUID = -7981116646708592917L;
    private String taskName;
    private String taskGroup;
    private String taskClass;
    private String taskMethod;
    private String initialParameter;

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

    public String getInitialParameter() {
        return initialParameter;
    }

    public void setInitialParameter(String initialParameter) {
        this.initialParameter = initialParameter;
    }

    @Override
    public String toString() {
        return String.format("Task '%s#%s' will call %s in %s", taskGroup, taskName, taskMethod,
                taskClass);
    }

}
