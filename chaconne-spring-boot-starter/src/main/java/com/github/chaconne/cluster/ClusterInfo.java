package com.github.chaconne.cluster;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @Description: ClusterInfo
 * @Author: Fred Feng
 * @Date: 20/04/2025
 * @Version 1.0.0
 */
public class ClusterInfo extends HashMap<String, String> {

    private static final long serialVersionUID = -1352127571275072394L;

    private static final String[] requiredFields =
            {"group", "memberId", "host", "port", "contextPath", "uptime"};

    public ClusterInfo() {
        super();
        put("uptime", String.valueOf(System.currentTimeMillis()));
    }

    public ClusterInfo(Map<String, String> map) {
        super(map);
        put("uptime", String.valueOf(System.currentTimeMillis()));
    }

    public String getGroup() {
        return get("group");
    }

    public void setGroup(String group) {
        put("group", group);
    }

    public String getMemberId() {
        return get("memberId");
    }

    public void setMemberId(String memberId) {
        put("memberId", memberId);
    }

    public String getHost() {
        return get("host");
    }

    public void setHost(String host) {
        put("host", host);
    }

    public int getPort() {
        return Integer.parseInt(get("port"));
    }

    public void setPort(int port) {
        put("port", String.valueOf(port));
    }

    public String getContextPath() {
        return get("contextPath");
    }

    public void setContextPath(String contextPath) {
        put("contextPath", contextPath);
    }

    public long getUptime() {
        return Long.parseLong(get("uptime"));
    }

    public Map<String, String> getMetadata() {
        Map<String, String> copy = new HashMap<String, String>(this);
        for (String field : requiredFields) {
            copy.remove(field);
        }
        return copy;
    }
}
