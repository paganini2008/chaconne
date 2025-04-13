package com.github.chaconne.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @Description: UpdatePolicy
 * @Author: Fred Feng
 * @Date: 11/04/2025
 * @Version 1.0.0
 */
public enum UpdatePolicy {

    NONE, CREATE, MERGE, REBUILD;

    @JsonValue
    public String getValue() {
        return this.name().toUpperCase();
    }

    @JsonCreator
    public static UpdatePolicy forName(String name) {
        return UpdatePolicy.valueOf(name.toUpperCase());
    }

}
