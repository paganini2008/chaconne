package indi.atlantis.framework.chaconne;

/**
 * 
 * ScheduleManager
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface ScheduleManager {

	JobState schedule(Job job) throws Exception;

	boolean hasScheduled(JobKey jobKey);

	JobState unscheduleJob(JobKey jobKey) throws Exception;

	void doSchedule();

	int countOfScheduling();

	JobFuture getJobFuture(JobKey jobKey);

}
