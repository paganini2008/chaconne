package com.github.chaconne;

import java.util.List;

/**
 * 
 * @Description: TaskMemberManager
 * @Author: Fred Feng
 * @Date: 15/04/2025
 * @Version 1.0.0
 */
public interface TaskMemberManager {

    void addTaskExecutor(TaskMember taskMember);

    int positionOfScheduler(TaskMember taskMember);

    int positionOfExecutor(TaskMember taskMember);

    int countOfScheduler();

    int countOfExecutor();

    List<TaskMember> findSchedulers(String group);

    List<TaskMember> findExecutors(String group);

    List<String> getExecutorGroups();

}
