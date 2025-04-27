package com.github.chaconne.cluster;

import org.springframework.context.ApplicationEvent;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: TaskMemberRemovedEvent
 * @Author: Fred Feng
 * @Date: 15/04/2025
 * @Version 1.0.0
 */
public class TaskMemberRemovedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1983688876216871856L;

    public TaskMemberRemovedEvent(Object source, TaskMember taskMember, ClusterMode clusterMode) {
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
