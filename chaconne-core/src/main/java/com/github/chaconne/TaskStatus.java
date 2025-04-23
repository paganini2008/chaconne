package com.github.chaconne;

import com.github.chaconne.utils.EnumConstant;

/**
 * 
 * @Description: TaskStatus
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public enum TaskStatus implements EnumConstant {

    NONE, STANDBY, SCHEDULED, RUNNING, PAUSED, FINISHED, CANCELED;

    @Override
    public Object getValue() {
        return this.name().toUpperCase();
    }

    @Override
    public String getRepr() {
        return (String) getValue();
    }

    public static TaskStatus forName(String name) {
        return TaskStatus.valueOf(name);
    }

}
