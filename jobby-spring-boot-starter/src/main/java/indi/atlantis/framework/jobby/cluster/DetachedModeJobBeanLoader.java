package indi.atlantis.framework.jobby.cluster;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.jobby.Job;
import indi.atlantis.framework.jobby.JobBeanLoader;
import indi.atlantis.framework.jobby.JobKey;
import indi.atlantis.framework.jobby.JobManager;
import indi.atlantis.framework.jobby.model.JobTriggerDetail;
import indi.atlantis.framework.seafloor.utils.ApplicationContextUtils;

/**
 * 
 * DetachedModeJobBeanLoader
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class DetachedModeJobBeanLoader implements JobBeanLoader {

	@Autowired
	private JobManager jobManager;

	@Override
	public Job loadJobBean(JobKey jobKey) throws Exception {
		final JobTriggerDetail triggerDetail = jobManager.getJobTriggerDetail(jobKey);
		return ApplicationContextUtils.autowireBean(new DetachedModeJobBeanProxy(jobKey, triggerDetail));

	}

}
