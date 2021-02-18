package indi.atlantis.framework.jobhub.server;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.jobhub.Job;
import indi.atlantis.framework.jobhub.JobBeanLoader;
import indi.atlantis.framework.jobhub.JobKey;
import indi.atlantis.framework.jobhub.JobManager;
import indi.atlantis.framework.jobhub.model.JobTriggerDetail;
import indi.atlantis.framework.seafloor.utils.ApplicationContextUtils;

/**
 * 
 * ServerModeJobBeanLoader
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ServerModeJobBeanLoader implements JobBeanLoader {

	@Autowired
	private JobManager jobManager;

	@Override
	public Job loadJobBean(JobKey jobKey) throws Exception {
		final JobTriggerDetail triggerDetail = jobManager.getJobTriggerDetail(jobKey);
		return ApplicationContextUtils.autowireBean(new ServerModeJobBeanProxy(jobKey, triggerDetail));

	}

}
