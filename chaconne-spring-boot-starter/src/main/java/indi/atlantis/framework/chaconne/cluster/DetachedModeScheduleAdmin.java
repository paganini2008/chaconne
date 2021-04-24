package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.chaconne.Job;
import indi.atlantis.framework.chaconne.JobBeanLoader;
import indi.atlantis.framework.chaconne.JobException;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobState;
import indi.atlantis.framework.chaconne.ScheduleAdmin;
import indi.atlantis.framework.chaconne.ScheduleManager;

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
