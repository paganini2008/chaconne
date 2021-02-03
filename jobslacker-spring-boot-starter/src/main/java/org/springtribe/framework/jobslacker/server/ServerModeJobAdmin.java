package org.springtribe.framework.jobslacker.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springtribe.framework.jobslacker.JobAdmin;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobLifeCycle;
import org.springtribe.framework.jobslacker.JobState;
import org.springtribe.framework.jobslacker.model.JobLifeCycleParam;
import org.springtribe.framework.jobslacker.model.JobParam;
import org.springtribe.framework.jobslacker.model.JobResult;

/**
 * 
 * ServerModeJobAdmin
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ServerModeJobAdmin implements JobAdmin {

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Override
	public JobState triggerJob(JobKey jobKey, Object attachment) throws Exception {
		ResponseEntity<JobResult<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/triggerJob",
				HttpMethod.POST, new JobParam(jobKey, attachment, 0), new ParameterizedTypeReference<JobResult<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public void publicLifeCycleEvent(JobKey jobKey, JobLifeCycle lifeCycle) {
		ResponseEntity<JobResult<String>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/publicLifeCycleEvent",
				HttpMethod.POST, new JobLifeCycleParam(jobKey, lifeCycle), new ParameterizedTypeReference<JobResult<String>>() {
				});
		responseEntity.getBody();
	}

}
