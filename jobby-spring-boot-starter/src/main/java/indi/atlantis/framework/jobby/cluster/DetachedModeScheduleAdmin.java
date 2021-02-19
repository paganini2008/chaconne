package indi.atlantis.framework.jobby.cluster;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.jobby.Job;
import indi.atlantis.framework.jobby.JobBeanLoader;
import indi.atlantis.framework.jobby.JobException;
import indi.atlantis.framework.jobby.JobKey;
import indi.atlantis.framework.jobby.JobState;
import indi.atlantis.framework.jobby.ScheduleAdmin;
import indi.atlantis.framework.jobby.ScheduleManager;

/**
 * 
 * DetachedModeScheduleAdmin
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class DetachedModeScheduleAdmin implements ScheduleAdmin {

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
