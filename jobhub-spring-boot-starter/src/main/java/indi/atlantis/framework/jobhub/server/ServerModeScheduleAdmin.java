package indi.atlantis.framework.jobhub.server;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.jobhub.Job;
import indi.atlantis.framework.jobhub.JobBeanLoader;
import indi.atlantis.framework.jobhub.JobException;
import indi.atlantis.framework.jobhub.JobKey;
import indi.atlantis.framework.jobhub.JobState;
import indi.atlantis.framework.jobhub.ScheduleAdmin;
import indi.atlantis.framework.jobhub.ScheduleManager;

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
