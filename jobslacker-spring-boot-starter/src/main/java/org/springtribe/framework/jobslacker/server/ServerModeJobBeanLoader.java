package org.springtribe.framework.jobslacker.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.cluster.utils.ApplicationContextUtils;
import org.springtribe.framework.jobslacker.Job;
import org.springtribe.framework.jobslacker.JobBeanLoader;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobManager;
import org.springtribe.framework.jobslacker.model.JobTriggerDetail;

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
