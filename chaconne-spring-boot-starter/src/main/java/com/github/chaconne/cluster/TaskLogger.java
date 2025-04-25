package com.github.chaconne.cluster;

import java.time.LocalDateTime;
import org.jooq.DSLContext;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskListener;

/**
 * 
 * @Description: LogTaskListener
 * @Author: Fred Feng
 * @Date: 25/04/2025
 * @Version 1.0.0
 */
public class TaskLogger implements TaskListener {

    private final DSLContext dslContext;

    public TaskLogger(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public void onTaskScheduled(LocalDateTime ldt, TaskDetail taskDetail) {
        // TODO Auto-generated method stub
        TaskListener.super.onTaskScheduled(ldt, taskDetail);
    }

    @Override
    public void onTaskBegan(LocalDateTime ldt, TaskDetail taskDetail) {
        // TODO Auto-generated method stub
        TaskListener.super.onTaskBegan(ldt, taskDetail);
    }

    @Override
    public void onTaskEnded(LocalDateTime ldt, TaskDetail taskDetail, Throwable e) {
        // TODO Auto-generated method stub
        TaskListener.super.onTaskEnded(ldt, taskDetail, e);
    }



}
