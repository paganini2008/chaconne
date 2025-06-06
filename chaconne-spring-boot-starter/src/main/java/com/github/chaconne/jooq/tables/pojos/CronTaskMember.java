/*
 * This file is generated by jOOQ.
 */
package com.github.chaconne.jooq.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class CronTaskMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memberGroup;
    private String memberId;
    private String host;
    private Integer port;
    private Byte isSsl;
    private Long uptime;
    private String contextPath;
    private String pingUrl;

    public CronTaskMember() {}

    public CronTaskMember(CronTaskMember value) {
        this.memberGroup = value.memberGroup;
        this.memberId = value.memberId;
        this.host = value.host;
        this.port = value.port;
        this.isSsl = value.isSsl;
        this.uptime = value.uptime;
        this.contextPath = value.contextPath;
        this.pingUrl = value.pingUrl;
    }

    public CronTaskMember(
        String memberGroup,
        String memberId,
        String host,
        Integer port,
        Byte isSsl,
        Long uptime,
        String contextPath,
        String pingUrl
    ) {
        this.memberGroup = memberGroup;
        this.memberId = memberId;
        this.host = host;
        this.port = port;
        this.isSsl = isSsl;
        this.uptime = uptime;
        this.contextPath = contextPath;
        this.pingUrl = pingUrl;
    }

    /**
     * Getter for <code>cron_task_member.member_group</code>.
     */
    public String getMemberGroup() {
        return this.memberGroup;
    }

    /**
     * Setter for <code>cron_task_member.member_group</code>.
     */
    public CronTaskMember setMemberGroup(String memberGroup) {
        this.memberGroup = memberGroup;
        return this;
    }

    /**
     * Getter for <code>cron_task_member.member_id</code>.
     */
    public String getMemberId() {
        return this.memberId;
    }

    /**
     * Setter for <code>cron_task_member.member_id</code>.
     */
    public CronTaskMember setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    /**
     * Getter for <code>cron_task_member.HOST</code>.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Setter for <code>cron_task_member.HOST</code>.
     */
    public CronTaskMember setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Getter for <code>cron_task_member.PORT</code>.
     */
    public Integer getPort() {
        return this.port;
    }

    /**
     * Setter for <code>cron_task_member.PORT</code>.
     */
    public CronTaskMember setPort(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * Getter for <code>cron_task_member.is_ssl</code>.
     */
    public Byte getIsSsl() {
        return this.isSsl;
    }

    /**
     * Setter for <code>cron_task_member.is_ssl</code>.
     */
    public CronTaskMember setIsSsl(Byte isSsl) {
        this.isSsl = isSsl;
        return this;
    }

    /**
     * Getter for <code>cron_task_member.uptime</code>.
     */
    public Long getUptime() {
        return this.uptime;
    }

    /**
     * Setter for <code>cron_task_member.uptime</code>.
     */
    public CronTaskMember setUptime(Long uptime) {
        this.uptime = uptime;
        return this;
    }

    /**
     * Getter for <code>cron_task_member.context_path</code>.
     */
    public String getContextPath() {
        return this.contextPath;
    }

    /**
     * Setter for <code>cron_task_member.context_path</code>.
     */
    public CronTaskMember setContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    /**
     * Getter for <code>cron_task_member.ping_url</code>.
     */
    public String getPingUrl() {
        return this.pingUrl;
    }

    /**
     * Setter for <code>cron_task_member.ping_url</code>.
     */
    public CronTaskMember setPingUrl(String pingUrl) {
        this.pingUrl = pingUrl;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CronTaskMember other = (CronTaskMember) obj;
        if (this.memberGroup == null) {
            if (other.memberGroup != null)
                return false;
        }
        else if (!this.memberGroup.equals(other.memberGroup))
            return false;
        if (this.memberId == null) {
            if (other.memberId != null)
                return false;
        }
        else if (!this.memberId.equals(other.memberId))
            return false;
        if (this.host == null) {
            if (other.host != null)
                return false;
        }
        else if (!this.host.equals(other.host))
            return false;
        if (this.port == null) {
            if (other.port != null)
                return false;
        }
        else if (!this.port.equals(other.port))
            return false;
        if (this.isSsl == null) {
            if (other.isSsl != null)
                return false;
        }
        else if (!this.isSsl.equals(other.isSsl))
            return false;
        if (this.uptime == null) {
            if (other.uptime != null)
                return false;
        }
        else if (!this.uptime.equals(other.uptime))
            return false;
        if (this.contextPath == null) {
            if (other.contextPath != null)
                return false;
        }
        else if (!this.contextPath.equals(other.contextPath))
            return false;
        if (this.pingUrl == null) {
            if (other.pingUrl != null)
                return false;
        }
        else if (!this.pingUrl.equals(other.pingUrl))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.memberGroup == null) ? 0 : this.memberGroup.hashCode());
        result = prime * result + ((this.memberId == null) ? 0 : this.memberId.hashCode());
        result = prime * result + ((this.host == null) ? 0 : this.host.hashCode());
        result = prime * result + ((this.port == null) ? 0 : this.port.hashCode());
        result = prime * result + ((this.isSsl == null) ? 0 : this.isSsl.hashCode());
        result = prime * result + ((this.uptime == null) ? 0 : this.uptime.hashCode());
        result = prime * result + ((this.contextPath == null) ? 0 : this.contextPath.hashCode());
        result = prime * result + ((this.pingUrl == null) ? 0 : this.pingUrl.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CronTaskMember (");

        sb.append(memberGroup);
        sb.append(", ").append(memberId);
        sb.append(", ").append(host);
        sb.append(", ").append(port);
        sb.append(", ").append(isSsl);
        sb.append(", ").append(uptime);
        sb.append(", ").append(contextPath);
        sb.append(", ").append(pingUrl);

        sb.append(")");
        return sb.toString();
    }
}
