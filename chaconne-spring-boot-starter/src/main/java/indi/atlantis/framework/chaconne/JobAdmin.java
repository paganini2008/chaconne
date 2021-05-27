package indi.atlantis.framework.chaconne;

/**
 * 
 * JobAdmin
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface JobAdmin {

	JobState triggerJob(JobKey jobKey, Object attachment) throws Exception;

	void publicLifeCycleEvent(JobKey jobKey, JobLifeCycle lifeCycle);

}