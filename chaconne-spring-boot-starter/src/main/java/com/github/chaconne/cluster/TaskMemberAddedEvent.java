package com.github.chaconne.cluster;

import org.springframework.context.ApplicationEvent;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: TaskMemberAddedEvent
 * @Author: Fred Feng
 * @Date: 15/04/2025
 * @Version 1.0.0
 */
public class TaskMemberAddedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 7195863982389715213L;

    public TaskMemberAddedEvent(Object source, TaskMember taskMember, ClusterMode clusterMode) {
        super(source);
        this.taskMember = taskMember;
        this.clusterMode = clusterMode;
    }

    private final TaskMember taskMember;
    private final ClusterMode clusterMode;

    public TaskMember getTaskMember() {
        return taskMember;
    }

    public ClusterMode getClusterMode() {
        return clusterMode;
    }

}
