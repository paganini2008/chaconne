package indi.atlantis.framework.chaconne;

/**
 * 
 * ScheduleAdmin
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface ScheduleAdmin {

	JobState scheduleJob(JobKey jobKey);
	
	JobState unscheduleJob(JobKey jobKey);
	
}
