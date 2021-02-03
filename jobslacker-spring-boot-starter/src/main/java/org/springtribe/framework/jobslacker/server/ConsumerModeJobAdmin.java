package org.springtribe.framework.jobslacker.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springtribe.framework.cluster.multicast.ApplicationMulticastGroup;
import org.springtribe.framework.jobslacker.BeanNames;
import org.springtribe.framework.jobslacker.Job;
import org.springtribe.framework.jobslacker.JobAdmin;
import org.springtribe.framework.jobslacker.JobBeanLoader;
import org.springtribe.framework.jobslacker.JobExecutor;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobLifeCycle;
import org.springtribe.framework.jobslacker.JobManager;
import org.springtribe.framework.jobslacker.JobState;
import org.springtribe.framework.jobslacker.LifeCycleListenerContainer;
import org.springtribe.framework.jobslacker.model.JobLifeCycleParam;

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
