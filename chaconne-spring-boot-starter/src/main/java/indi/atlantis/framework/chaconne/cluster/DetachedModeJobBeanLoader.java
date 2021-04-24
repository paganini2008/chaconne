package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.chaconne.Job;
import indi.atlantis.framework.chaconne.JobBeanLoader;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.model.JobTriggerDetail;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;

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
