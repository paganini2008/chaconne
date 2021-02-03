package org.springtribe.framework.jobslacker.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.TraceIdGenerator;
import org.springtribe.framework.jobslacker.model.JobResult;

/**
 * 
 * RestTraceIdGenerator
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class RestTraceIdGenerator implements TraceIdGenerator {

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Override
	public long generateTraceId(JobKey jobKey) {
		ResponseEntity<JobResult<Long>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/generateTraceId",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<JobResult<Long>>() {
				});
		return responseEntity.getBody().getData();
	}

}
