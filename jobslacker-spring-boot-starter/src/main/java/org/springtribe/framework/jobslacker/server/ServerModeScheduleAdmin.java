package org.springtribe.framework.jobslacker.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.jobslacker.Job;
import org.springtribe.framework.jobslacker.JobBeanLoader;
import org.springtribe.framework.jobslacker.JobException;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobState;
import org.springtribe.framework.jobslacker.ScheduleAdmin;
import org.springtribe.framework.jobslacker.ScheduleManager;

/**
 * 
 * ServerModeScheduleAdmin
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ServerModeScheduleAdmin implements ScheduleAdmin {

	@Autowired
	private JobBeanLoader jobBeanLoader;

	@Autowired
	private ScheduleManager scheduleManager;

	@Override
	public JobState scheduleJob(JobKey jobKey) {
		try {
			Job job = jobBeanLoader.loadJobBean(jobKey);
			return scheduleManager.schedule(job);
		} catch (Exception e) {
			throw new JobException(e.getMessage(), e);
		}
	}

	@Override
	public JobState unscheduleJob(JobKey jobKey) {
		try {
			return scheduleManager.unscheduleJob(jobKey);
		} catch (Exception e) {
			throw new JobException(e.getMessage(), e);
		}
	}

}
