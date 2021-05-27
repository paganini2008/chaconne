package indi.atlantis.framework.chaconne;

/**
 * 
 * SerialDependencyScheduler
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface SerialDependencyScheduler {

	JobFuture scheduleDependency(Job job, JobKey... dependencies);

	boolean hasScheduled(JobKey jobKey);

	void updateDependency(Job job, JobKey... dependencies);

	void triggerDependency(JobKey jobKey, Object attachment);

	void notifyDependants(JobKey jobKey, Object result);

}