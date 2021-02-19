package indi.atlantis.framework.jobby.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import indi.atlantis.framework.jobby.JobAdmin;
import indi.atlantis.framework.jobby.JobKey;
import indi.atlantis.framework.jobby.JobLifeCycle;
import indi.atlantis.framework.jobby.JobState;
import indi.atlantis.framework.jobby.model.JobLifeCycleParam;
import indi.atlantis.framework.jobby.model.JobParam;
import indi.atlantis.framework.jobby.model.JobResult;

/**
 * 
 * DetachedModeJobAdmin
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class DetachedModeJobAdmin implements JobAdmin {

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
