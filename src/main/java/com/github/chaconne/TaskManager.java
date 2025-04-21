package com.github.chaconne;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * @Description: TaskManager
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface TaskManager {

    TaskDetail saveTask(Task task, String initialParameter) throws ChaconneException;

    TaskDetail removeTask(TaskId taskId) throws ChaconneException;

    TaskDetail getTaskDetail(TaskId taskId) throws ChaconneException;

    boolean hasTask(TaskId taskId) throws ChaconneException;

    default String getInitialParameter(TaskId taskId) throws ChaconneException {
        TaskDetail taskDetail = getTaskDetail(taskId);
        return taskDetail != null ? taskDetail.getInitialParameter() : null;
    }

    default TaskStatus getTaskStatus(TaskId taskId) throws ChaconneException {
        TaskDetail taskDetail = getTaskDetail(taskId);
        return taskDetail != null ? taskDetail.getTaskStatus() : null;
    }

    int getTaskCount(String group, String name) throws ChaconneException;

    List<TaskDetailVo> findTaskDetails(String group, String name, int limit, int offset)
            throws ChaconneException;

    List<LocalDateTime> findNextFiredDateTimes(TaskId taskId, LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws ChaconneException;

    List<TaskId> findUpcomingTasksBetween(LocalDateTime startDateTime, LocalDateTime endDateTime)
            throws ChaconneException;

    LocalDateTime computeNextFiredDateTime(TaskId taskId, LocalDateTime previousFiredDateTime)
            throws ChaconneException;

    void setTaskStatus(TaskId taskId, TaskStatus status) throws ChaconneException;

}
