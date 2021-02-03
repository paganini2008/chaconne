package org.springtribe.framework.jobslacker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.cluster.utils.ApplicationContextUtils;
import org.springtribe.framework.jobslacker.model.JobTriggerDetail;

/**
 * 
 * ExternalJobBeanLoader
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ExternalJobBeanLoader implements JobBeanLoader {

	@Autowired
	private JobManager jobManager;

	@Override
	public Job loadJobBean(JobKey jobKey) throws Exception {
		final JobTriggerDetail triggerDetail = jobManager.getJobTriggerDetail(jobKey);
		return ApplicationContextUtils.autowireBean(new ExternalJobBeanProxy(jobKey, triggerDetail));
	}

}
