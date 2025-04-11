package com.github.chaconne;

/**
 * 
 * @Description: TaskId
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface TaskId {

    String DEFAULT_GROUP = "default";

    default String getGroup() {
        return DEFAULT_GROUP;
    }

    default String getName() {
        return "";
    }

    static TaskId of(String name) {
        return new DefaultTaskId(DEFAULT_GROUP, name);
    }

    static TaskId of(String group, String name) {
        return new DefaultTaskId(group, name);
    }
}
