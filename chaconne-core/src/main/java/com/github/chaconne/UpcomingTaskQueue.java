package com.github.chaconne;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 
 * @Description: UpcomingTaskQueue
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface UpcomingTaskQueue {

    boolean addTask(LocalDateTime ldt, TaskId taskId);

    int length();

    Collection<TaskId> matchTaskIds(LocalDateTime ldt);

}
