package org.springtribe.framework.jobslacker.server;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;
import org.springtribe.framework.jobslacker.GenericTrigger;
import org.springtribe.framework.jobslacker.Job;
import org.springtribe.framework.jobslacker.JobAdmin;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobManager;
import org.springtribe.framework.jobslacker.JobState;
import org.springtribe.framework.jobslacker.Trigger;
import org.springtribe.framework.jobslacker.model.JobTriggerDetail;

/**
 * 
 * ServerModeJobBeanProxy
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ServerModeJobBeanProxy implements Job {

	private final JobKey jobKey;
	private final JobTriggerDetail triggerDetail;

	@Autowired
	private JobAdmin jobAdmin;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private ClusterRegistry clusterRegistry;

	public ServerModeJobBeanProxy(JobKey jobKey, JobTriggerDetail triggerDetail) {
		this.jobKey = jobKey;
		this.triggerDetail = triggerDetail;
	}

	@Override
	public String getClusterName() {
		return jobKey.getClusterName();
	}

	@Override
	public String getJobName() {
		return jobKey.getJobName();
	}

	@Override
	public String getJobClassName() {
		return jobKey.getJobClassName();
	}

	@Override
	public String getGroupName() {
		return jobKey.getGroupName();
	}

	@Override
	public Trigger getTrigger() {
		return GenericTrigger.Builder.newTrigger().setTriggerType(triggerDetail.getTriggerType())
				.setTriggerDescription(triggerDetail.getTriggerDescriptionObject()).setStartDate(triggerDetail.getStartDate())
				.setEndDate(triggerDetail.getEndDate()).setRepeatCount(triggerDetail.getRepeatCount()).build();
	}

	@Override
	public Object execute(JobKey jobKey, Object result, Logger log) {
		try {
			return jobAdmin.triggerJob(jobKey, result);
		} catch (RestClientException e) {
			resetJobState();
			clusterRegistry.unregisterCluster(jobKey.getClusterName());
			log.error(e.getMessage(), e);
		} catch (NoJobResourceException e) {
			resetJobState();
			log.warn("Job: " + jobKey.toString() + " is not available now.");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return JobState.NONE;
	}

	private void resetJobState() {
		try {
			jobManager.setJobState(jobKey, JobState.SCHEDULING);
		} catch (Exception ignored) {
		}
	}

	@Override
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		log.error(e.getMessage(), e);
	}

}
