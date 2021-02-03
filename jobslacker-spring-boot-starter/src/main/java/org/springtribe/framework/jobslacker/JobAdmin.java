package org.springtribe.framework.jobslacker;

/**
 * 
 * JobAdmin
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface JobAdmin {

	JobState triggerJob(JobKey jobKey, Object attachment) throws Exception;

	void publicLifeCycleEvent(JobKey jobKey, JobLifeCycle lifeCycle);

}