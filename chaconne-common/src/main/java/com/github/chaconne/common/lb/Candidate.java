package com.github.chaconne.common.lb;

/**
 * 
 * @Description: Candidate
 * @Author: Fred Feng
 * @Date: 25/05/2025
 * @Version 1.0.0
 */
public interface Candidate {

    String getName();

    String getServerAddress();

    default String getPingUrl() {
        return "/ping";
    }

}
