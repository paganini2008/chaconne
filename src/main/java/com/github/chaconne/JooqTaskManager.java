package com.github.chaconne;

import java.time.LocalDateTime;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.cronsmith.scheduler.CronTaskException;

/**
 * 
 * @Description: JooqTaskManager
 * @Author: Fred Feng
 * @Date: 10/04/2025
 * @Version 1.0.0
 */
public class JooqTaskManager implements TaskManager {

    @Autowired
    private DSLContext dsl;

    @Override
    public TaskDetail saveTask(Task task, String initialParameter) throws CronTaskException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TaskDetail removeTask(TaskId taskId) throws CronTaskException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TaskDetail getTaskDetail(TaskId taskId) throws CronTaskException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasTask(TaskId taskId) throws CronTaskException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getTaskCount(String group, String name) throws CronTaskException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<TaskInfoVo> findTaskInfos(String group, String name, int limit, int offset)
            throws CronTaskException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LocalDateTime> findNextFiredDateTimes(TaskId taskId, LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws CronTaskException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TaskId> findUpcomingTasksBetween(LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws CronTaskException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDateTime computeNextFiredDateTime(TaskId taskId,
            LocalDateTime previousFiredDateTime) throws CronTaskException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTaskStatus(TaskId taskId, TaskStatus status) throws CronTaskException {
        // TODO Auto-generated method stub

    }

}
