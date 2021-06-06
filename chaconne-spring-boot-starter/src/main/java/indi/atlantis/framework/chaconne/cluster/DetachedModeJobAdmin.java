package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import indi.atlantis.framework.chaconne.JobAdmin;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobLifeCycle;
import indi.atlantis.framework.chaconne.JobState;
import indi.atlantis.framework.chaconne.model.JobLifeCycleParameter;
import indi.atlantis.framework.chaconne.model.JobParameter;
import indi.atlantis.framework.chaconne.model.JobResult;

/**
 * 
 * DetachedModeJobAdmin
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class DetachedModeJobAdmin implements JobAdmin {

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Override
	public JobState triggerJob(JobKey jobKey, Object attachment) throws Exception {
		ResponseEntity<JobResult<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/triggerJob",
				HttpMethod.POST, new JobParameter(jobKey, attachment, 0), new ParameterizedTypeReference<JobResult<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public void publicLifeCycleEvent(JobKey jobKey, JobLifeCycle lifeCycle) {
		ResponseEntity<JobResult<String>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/publicLifeCycleEvent",
				HttpMethod.POST, new JobLifeCycleParameter(jobKey, lifeCycle), new ParameterizedTypeReference<JobResult<String>>() {
				});
		responseEntity.getBody();
	}

}
