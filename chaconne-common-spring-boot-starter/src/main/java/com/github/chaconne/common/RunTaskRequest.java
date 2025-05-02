package com.github.chaconne.common;

import java.io.Serializable;

/**
 * 
 * @Description: RunTaskRequest
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public class RunTaskRequest extends TaskIdRequest implements Serializable {

    private static final long serialVersionUID = -7981116646708592917L;
    private String taskClass;
    private String taskMethod;
    private String initialParameter;

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
        return String.format("Task '%s#%s' will call %s in %s", getTaskGroup(), getTaskName(),
                taskMethod, taskClass);
    }

}
