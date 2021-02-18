package indi.atlantis.framework.jobhub.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.jobhub.BeanNames;
import indi.atlantis.framework.jobhub.Job;
import indi.atlantis.framework.jobhub.JobAdmin;
import indi.atlantis.framework.jobhub.JobBeanLoader;
import indi.atlantis.framework.jobhub.JobExecutor;
import indi.atlantis.framework.jobhub.JobKey;
import indi.atlantis.framework.jobhub.JobLifeCycle;
import indi.atlantis.framework.jobhub.JobManager;
import indi.atlantis.framework.jobhub.JobState;
import indi.atlantis.framework.jobhub.LifeCycleListenerContainer;
import indi.atlantis.framework.jobhub.model.JobLifeCycleParam;
import indi.atlantis.framework.seafloor.multicast.ApplicationMulticastGroup;

/**
 * 
 * ConsumerModeJobAdmin
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ConsumerModeJobAdmin implements JobAdmin {

	@Value("${spring.application.name}")
	private String applicationName;

	@Qualifier(BeanNames.MAIN_JOB_EXECUTOR)
	@Autowired
	private JobExecutor jobExecutor;

	@Qualifier(BeanNames.INTERNAL_JOB_BEAN_LOADER)
	@Autowired
	private JobBeanLoader jobBeanLoader;

	@Qualifier(BeanNames.EXTERNAL_JOB_BEAN_LOADER)
	@Autowired(required = false)
	private JobBeanLoader externalJobBeanLoader;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private ApplicationMulticastGroup multicastGroup;

	@Override
	public JobState triggerJob(JobKey jobKey, Object attachment) throws Exception {
		Job job = loadJobBean(jobKey);
		jobExecutor.execute(job, attachment, 0);
		return jobManager.getJobRuntime(jobKey).getJobState();
	}

	@Override
	public void publicLifeCycleEvent(JobKey jobKey, JobLifeCycle lifeCycle) {
		multicastGroup.multicast(applicationName, LifeCycleListenerContainer.class.getName(),
				new JobLifeCycleParam(jobKey, lifeCycle));
	}

	private Job loadJobBean(JobKey jobKey) throws Exception {
		Job job = jobBeanLoader.loadJobBean(jobKey);
		if (job == null && externalJobBeanLoader != null) {
			job = externalJobBeanLoader.loadJobBean(jobKey);
		}
		return job;
	}

}
