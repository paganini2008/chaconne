package com.github.chaconne.client;

import org.springframework.context.ApplicationEvent;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: TaskMemberRegisteredEvent
 * @Author: Fred Feng
 * @Date: 26/05/2025
 * @Version 1.0.0
 */
public class TaskMemberRegisteredEvent extends ApplicationEvent {

    private static final long serialVersionUID = 6425366322674907097L;

    public TaskMemberRegisteredEvent(Object source, TaskMember taskMember) {
        super(source);
        this.taskMember = taskMember;
    }

    private final TaskMember taskMember;

    public TaskMember getTaskMember() {
        return taskMember;
    }

}
