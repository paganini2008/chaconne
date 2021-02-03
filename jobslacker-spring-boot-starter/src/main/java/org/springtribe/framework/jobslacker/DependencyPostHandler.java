package org.springtribe.framework.jobslacker;

/**
 * 
 * DependencyPostHandler
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface DependencyPostHandler {

	boolean approve(JobKey jobKey, RunningState runningState, Object attachment, Object result);

}
