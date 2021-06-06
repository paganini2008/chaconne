package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.chaconne.ChaconneBeanNames;
import indi.atlantis.framework.chaconne.Job;
import indi.atlantis.framework.chaconne.JobAdmin;
import indi.atlantis.framework.chaconne.JobBeanLoader;
import indi.atlantis.framework.chaconne.JobExecutor;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobLifeCycle;
import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.JobState;
import indi.atlantis.framework.chaconne.LifeCycleListenerContainer;
import indi.atlantis.framework.chaconne.model.JobLifeCycleParameter;
import indi.atlantis.framework.tridenter.multicast.ApplicationMulticastGroup;

/**
 * 
 * ConsumerModeJobAdmin
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class ConsumerModeJobAdmin implements JobAdmin {

	@Value("${spring.application.name}")
	private String applicationName;

	@Qualifier(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
	@Autowired
	private JobExecutor jobExecutor;

	@Qualifier(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
	@Autowired
	private JobBeanLoader jobBeanLoader;

	@Qualifier(ChaconneBeanNames.EXTERNAL_JOB_BEAN_LOADER)
	@Autowired(required = false)
	private JobBeanLoader externalJobBeanLoader;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private ApplicationMulticastGroup applicationMulticastGroup;

	@Override
	public JobState triggerJob(JobKey jobKey, Object attachment) throws Exception {
		Job job = loadJobBean(jobKey);
		jobExecutor.execute(job, attachment, 0);
		return jobManager.getJobRuntimeDetail(jobKey).getJobState();
	}

	@Override
	public void publicLifeCycleEvent(JobKey jobKey, JobLifeCycle lifeCycle) {
		applicationMulticastGroup.multicast(applicationName, LifeCycleListenerContainer.class.getName(),
				new JobLifeCycleParameter(jobKey, lifeCycle));
	}

	private Job loadJobBean(JobKey jobKey) throws Exception {
		Job job = jobBeanLoader.loadJobBean(jobKey);
		if (job == null && externalJobBeanLoader != null) {
			job = externalJobBeanLoader.loadJobBean(jobKey);
		}
		return job;
	}

}
