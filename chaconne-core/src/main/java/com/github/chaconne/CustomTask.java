package com.github.chaconne;

/**
 * 
 * @Description: CustomTask
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public interface CustomTask extends Task {

    String getTaskClassName();

    String getTaskMethodName();

    default String getUrl() {
        return "";
    }

}
