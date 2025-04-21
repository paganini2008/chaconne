package com.github.chaconne.cluster;

/**
 * 
 * @Description: MemberLock
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
public class TaskMemberLock {

    private final TaskMemberManager taskMemberManager;
    private final TaskMember currentTaskMember;

    public TaskMemberLock(TaskMemberManager taskMemberManager, TaskMember currentTaskMember) {
        this.taskMemberManager = taskMemberManager;
        this.currentTaskMember = currentTaskMember;
    }

    public boolean tryLock() {
        TaskMember firstMember = taskMemberManager.getSchedulers().peek();
        return firstMember != null && firstMember.equals(currentTaskMember);
    }
}
