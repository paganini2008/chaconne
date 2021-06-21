package indi.atlantis.framework.chaconne.cluster;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobState;
import indi.atlantis.framework.chaconne.RunningState;
import indi.atlantis.framework.chaconne.StopWatch;
import indi.atlantis.framework.chaconne.model.Result;
import indi.atlantis.framework.chaconne.model.JobRuntimeParameter;

/**
 * 
 * RestStopWatch
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class RestStopWatch implements StopWatch {

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Async
	@Override
	public JobState onJobBegin(long traceId, JobKey jobKey, Date startDate) {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/onJobBegin",
				HttpMethod.POST, new JobRuntimeParameter(traceId, jobKey, startDate), new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Async
	@Override
	public JobState onJobEnd(long traceId, JobKey jobKey, Date startDate, RunningState runningState, int retries) {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/onJobEnd",
				HttpMethod.POST, new JobRuntimeParameter(traceId, jobKey, startDate, runningState, retries),
				new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();

	}

}
