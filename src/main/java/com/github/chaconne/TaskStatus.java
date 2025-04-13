package com.github.chaconne;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @Description: TaskStatus
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public enum TaskStatus implements EnumConstant {

    NONE, STANDBY, SCHEDULED, RUNNING, PAUSED, FINISHED, CANCELED;

    @JsonValue
    @Override
    public Object getValue() {
        return this.name().toUpperCase();
    }

    @Override
    public String getRepr() {
        return (String) getValue();
    }

    @JsonCreator
    public static TaskStatus forName(String name) {
        return TaskStatus.valueOf(name);
    }

}
