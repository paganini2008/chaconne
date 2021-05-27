package indi.atlantis.framework.chaconne;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.chaconne.model.JobTriggerDetail;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;

/**
 * 
 * ExternalJobBeanLoader
 * 
 * @author Fred Feng
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
