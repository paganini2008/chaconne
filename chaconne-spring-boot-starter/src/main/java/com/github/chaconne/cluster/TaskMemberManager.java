package com.github.chaconne.cluster;

import java.util.List;
import java.util.Queue;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: TaskMemberManager
 * @Author: Fred Feng
 * @Date: 15/04/2025
 * @Version 1.0.0
 */
public interface TaskMemberManager {

    void addExecutor(TaskMember taskMember);

    Queue<TaskMember> getSchedulers();

    List<TaskMember> getExecutors();

}
