package com.github.chaconne.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: LoggingTaskMemberChangeListener
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class LoggingTaskMemberChangeListener implements TaskMemberChangeListener {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingTaskMemberChangeListener.class);

    @Override
    public void onSchedulerAdded(TaskMember taskMember) {
        log.info("Scheduler {} added", taskMember);
    }

    @Override
    public void onSchedulerRemoved(TaskMember taskMember) {
        log.info("Scheduler {} removed", taskMember);
    }

    @Override
    public void onExecutorAdded(TaskMember taskMember) {
        log.info("Executor {} added", taskMember);
    }

    @Override
    public void onExecutorRemoved(TaskMember taskMember) {
        log.info("Executor {} removed", taskMember);
    }

}
