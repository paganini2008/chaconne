package com.github.chaconne.common.utils;

/**
 * 
 * @Description: EnumConstant
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public interface EnumConstant {

    String DEFAULT_GROUP = "DEFAULT";

    Object getValue();

    String getRepr();

    default String getGroup() {
        return DEFAULT_GROUP;
    }
}
