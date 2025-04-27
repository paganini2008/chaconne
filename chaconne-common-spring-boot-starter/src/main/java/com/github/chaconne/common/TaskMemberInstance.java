package com.github.chaconne.common;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * @Description: TaskMemberRegistration
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class TaskMemberInstance implements TaskMember, Serializable, Comparable<TaskMember> {

    private static final long serialVersionUID = -7791311095744563130L;
    private String group;
    private String memberId;
    private String host;
    private int port;
    private String contextPath;
    private long uptime;
    private Map<String, String> metadata;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public long getUptime() {
        return uptime;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "uptime", "metadata");
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "uptime", "metadata");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public int compareTo(TaskMember other) {
        long result = getUptime() - other.getUptime();
        if (result > 0) {
            return 1;
        }
        if (result < 0) {
            return -1;
        }
        return 0;
    }

}
