package com.github.chaconne;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * @Description: TaskMemberAddedEvent
 * @Author: Fred Feng
 * @Date: 15/04/2025
 * @Version 1.0.0
 */
public class TaskMemberAddedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 7195863982389715213L;

    public TaskMemberAddedEvent(Object source, TaskMember taskMember) {
        super(source);
        this.taskMember = taskMember;
    }

    private final TaskMember taskMember;

    public TaskMember getTaskMember() {
        return taskMember;
    }

}
