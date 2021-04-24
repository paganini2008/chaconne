package indi.atlantis.framework.chaconne.cluster;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import indi.atlantis.framework.chaconne.GenericTrigger;
import indi.atlantis.framework.chaconne.Job;
import indi.atlantis.framework.chaconne.JobAdmin;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.JobState;
import indi.atlantis.framework.chaconne.Trigger;
import indi.atlantis.framework.chaconne.model.JobTriggerDetail;

/**
 * 
 * DetachedModeJobBeanProxy
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class DetachedModeJobBeanProxy implements Job {

	private final JobKey jobKey;
	private final JobTriggerDetail triggerDetail;

	@Autowired
	private JobAdmin jobAdmin;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobServerRegistry clusterRegistry;

	public DetachedModeJobBeanProxy(JobKey jobKey, JobTriggerDetail triggerDetail) {
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
