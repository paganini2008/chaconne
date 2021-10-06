/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.atlantisframework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import io.atlantisframework.chaconne.ExceptionUtils;
import io.atlantisframework.chaconne.Job;
import io.atlantisframework.chaconne.JobAdmin;
import io.atlantisframework.chaconne.JobBeanLoader;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobLifeCycle;
import io.atlantisframework.chaconne.JobState;
import io.atlantisframework.chaconne.ScheduleManager;
import io.atlantisframework.chaconne.model.JobLifeCycleParameter;
import io.atlantisframework.chaconne.model.JobParameter;
import io.atlantisframework.chaconne.model.Result;

/**
 * 
 * DetachedModeJobAdmin
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class DetachedModeJobAdmin implements JobAdmin {

	@Autowired
	private JobBeanLoader jobBeanLoader;

	@Autowired
	private ScheduleManager scheduleManager;

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Override
	@Retryable(value = JobServiceAccessException.class, maxAttempts = 5, backoff = @Backoff(delay = 5000, multiplier = 1, maxDelay = 10000))
	public JobState triggerJob(JobKey jobKey, Object attachment) throws Exception {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/triggerJob",
				HttpMethod.POST, new JobParameter(jobKey, attachment, 0), new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	@Retryable(value = JobServiceAccessException.class, maxAttempts = 5, backoff = @Backoff(delay = 5000, multiplier = 1, maxDelay = 10000))
	public void publicLifeCycleEvent(JobKey jobKey, JobLifeCycle lifeCycle) {
		ResponseEntity<Result<String>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/publicLifeCycleEvent",
				HttpMethod.POST, new JobLifeCycleParameter(jobKey, lifeCycle), new ParameterizedTypeReference<Result<String>>() {
				});
		responseEntity.getBody();
	}

	@Override
	public JobState scheduleJob(JobKey jobKey) {
		try {
			Job job = jobBeanLoader.loadJobBean(jobKey);
			return scheduleManager.schedule(job);
		} catch (Exception e) {
			throw ExceptionUtils.wrapExeception(e);
		}
	}

	@Override
	public JobState unscheduleJob(JobKey jobKey) {
		try {
			return scheduleManager.unscheduleJob(jobKey);
		} catch (Exception e) {
			throw ExceptionUtils.wrapExeception(e);
		}
	}

}
