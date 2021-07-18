/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.atlantis.framework.chaconne.JobAdmin;
import indi.atlantis.framework.chaconne.JobState;
import indi.atlantis.framework.chaconne.model.JobLifeCycleParameter;
import indi.atlantis.framework.chaconne.model.JobParameter;
import indi.atlantis.framework.chaconne.model.Result;
import indi.atlantis.framework.tridenter.ApplicationInfo;
import indi.atlantis.framework.tridenter.InstanceId;

/**
 * 
 * ConsumerModeController
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@RestController
@RequestMapping("/job/admin")
public class ConsumerModeController {

	@Autowired
	private InstanceId instanceId;

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Autowired
	private JobAdmin jobAdmin;

	@GetMapping("/registerCluster")
	public ResponseEntity<Result<Boolean>> registerCluster() throws Exception {
		ApplicationInfo applicationInfo = instanceId.getApplicationInfo();
		return restTemplate.perform(null, "/job/admin/registerCluster", HttpMethod.POST, applicationInfo,
				new ParameterizedTypeReference<Result<Boolean>>() {
				});
	}

	@PostMapping("/triggerJob")
	public ResponseEntity<Result<JobState>> triggerJob(@RequestBody JobParameter jobParam) throws Exception {
		JobState jobState = jobAdmin.triggerJob(jobParam.getJobKey(), jobParam.getAttachment());
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/publicLifeCycleEvent")
	public ResponseEntity<Result<String>> publicLifeCycleEvent(@RequestBody JobLifeCycleParameter parameter) throws Exception {
		jobAdmin.publicLifeCycleEvent(parameter.getJobKey(), parameter.getLifeCycle());
		return ResponseEntity.ok(Result.success("ok"));
	}

}
