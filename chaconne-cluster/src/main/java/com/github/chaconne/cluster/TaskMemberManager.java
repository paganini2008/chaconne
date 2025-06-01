package com.github.chaconne.cluster;

import java.util.Collection;
import java.util.Set;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: TaskMemberManager
 * @Author: Fred Feng
 * @Date: 15/04/2025
 * @Version 1.0.0
 */
public interface TaskMemberManager {

    void addTaskMember(TaskMember taskMember);

    Set<String> getGroups();

    Collection<TaskMember> getTaskMembers(String group);

    void removeTaskMember(TaskMember taskMember);

}
