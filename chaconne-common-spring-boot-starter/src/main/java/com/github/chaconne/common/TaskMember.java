package com.github.chaconne.common;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @Description: TaskMember
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public interface TaskMember {

    String getGroup();

    String getMemberId();

    String getHost();

    int getPort();

    long getUptime();

    default String getContextPath() {
        return "";
    }

    default int getRole() {
        return 0;
    }

    default boolean isSsl() {
        return false;
    }

    default String getUrl() {
        String url = String.format("%s://%s:%s", isSsl() ? "https" : "http", getHost(), getPort());
        if (StringUtils.isNotBlank(getContextPath())) {
            url += getContextPath();
        }
        return url;
    }

    default String getPingUrl() {
        return "/chac/ping";
    }

}
