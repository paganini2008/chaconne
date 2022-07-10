/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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

import io.atlantisframework.chaconne.JobAdmin;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobLifeCycle;
import io.atlantisframework.chaconne.JobState;
import io.atlantisframework.chaconne.model.JobLifeCycleParameter;
import io.atlantisframework.chaconne.model.JobParameter;
import io.atlantisframework.chaconne.model.Result;

/**
 * 
 * RestJobAdmin
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class RestJobAdmin implements JobAdmin {

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Override
	public JobState triggerJob(JobKey jobKey, Object attachment) throws Exception {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/triggerJob",
				HttpMethod.POST, new JobParameter(jobKey, attachment, 0), new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public void publicLifeCycleEvent(JobKey jobKey, JobLifeCycle lifeCycle) {
		ResponseEntity<Result<String>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/publicLifeCycleEvent",
				HttpMethod.POST, new JobLifeCycleParameter(jobKey, lifeCycle), new ParameterizedTypeReference<Result<String>>() {
				});
		responseEntity.getBody();
	}

	@Override
	public JobState scheduleJob(JobKey jobKey) {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/scheduleJob",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobState unscheduleJob(JobKey jobKey) {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/admin/unscheduleJob",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

}
