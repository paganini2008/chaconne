package org.springtribe.framework.jobslacker;

/**
 * 
 * ScheduleAdmin
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface ScheduleAdmin {

	JobState scheduleJob(JobKey jobKey);
	
	JobState unscheduleJob(JobKey jobKey);
	
}
