package indi.atlantis.framework.jobby;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.jobby.model.JobTriggerDetail;
import indi.atlantis.framework.seafloor.utils.ApplicationContextUtils;

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
