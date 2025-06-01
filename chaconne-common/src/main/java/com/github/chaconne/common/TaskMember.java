package com.github.chaconne.common;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.github.chaconne.common.lb.Candidate;

/**
 * 
 * @Description: TaskMember
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public interface TaskMember extends Candidate {

    String getGroup();

    String getMemberId();

    String getHost();

    int getPort();

    long getUptime();

    Map<String, String> getMetadata();

    default String getContextPath() {
        return "";
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

    default String getName() {
        return getGroup();
    }

    default String getServerAddress() {
        return getUrl();
    }

}
