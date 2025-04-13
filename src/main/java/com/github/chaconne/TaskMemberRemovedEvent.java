package com.github.chaconne;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * @Description: TaskMemberRemovedEvent
 * @Author: Fred Feng
 * @Date: 15/04/2025
 * @Version 1.0.0
 */
public class TaskMemberRemovedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1983688876216871856L;

    public TaskMemberRemovedEvent(Object source, TaskMember taskMember) {
        super(source);
        this.taskMember = taskMember;
    }

    private final TaskMember taskMember;

    public TaskMember getTaskMember() {
        return taskMember;
    }

}
