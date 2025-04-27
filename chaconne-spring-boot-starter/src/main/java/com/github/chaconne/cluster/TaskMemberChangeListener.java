package com.github.chaconne.cluster;

import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: TaskMemberChangeListener
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public interface TaskMemberChangeListener {

    default void onSchedulerAdded(TaskMember taskMember) {}

    default void onSchedulerRemoved(TaskMember taskMember) {}

    default void onExecutorAdded(TaskMember taskMember) {}

    default void onExecutorRemoved(TaskMember taskMember) {}

}
