package indi.atlantis.framework.jobby.cluster;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import indi.atlantis.framework.jobby.JobKey;
import indi.atlantis.framework.jobby.JobState;
import indi.atlantis.framework.jobby.RunningState;
import indi.atlantis.framework.jobby.StopWatch;
import indi.atlantis.framework.jobby.model.JobResult;
import indi.atlantis.framework.jobby.model.JobRuntimeParam;

/**
 * 
 * RestStopWatch
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class RestStopWatch implements StopWatch {

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Async
	@Override
	public JobState onJobBegin(long traceId, JobKey jobKey, Date startDate) {
		ResponseEntity<JobResult<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/onJobBegin",
				HttpMethod.POST, new JobRuntimeParam(traceId, jobKey, startDate), new ParameterizedTypeReference<JobResult<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Async
	@Override
	public JobState onJobEnd(long traceId, JobKey jobKey, Date startDate, RunningState runningState, int retries) {
		ResponseEntity<JobResult<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/onJobEnd",
				HttpMethod.POST, new JobRuntimeParam(traceId, jobKey, startDate, runningState, retries),
				new ParameterizedTypeReference<JobResult<JobState>>() {
				});
		return responseEntity.getBody().getData();

	}

}
