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
package io.atlantisframework.chaconne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.atlantisframework.chaconne.model.JobLifeCycleParameter;
import io.atlantisframework.chaconne.model.JobParameter;
import io.atlantisframework.chaconne.model.Result;

/**
 * 
 * JobAdminController
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@RestController
@RequestMapping("/job/admin")
public class JobAdminController {

	@Autowired
	private JobAdmin jobAdmin;

	@PostMapping("/triggerJob")
	public ResponseEntity<Result<JobState>> triggerJob(@RequestBody JobParameter jobParam) throws Exception {
		JobState jobState = jobAdmin.triggerJob(jobParam.getJobKey(), jobParam.getAttachment());
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/publicLifeCycleEvent")
	public ResponseEntity<Result<String>> publicLifeCycleEvent(@RequestBody JobLifeCycleParameter jobParam) throws Exception {
		jobAdmin.publicLifeCycleEvent(jobParam.getJobKey(), jobParam.getLifeCycle());
		return ResponseEntity.ok(Result.success("ok"));
	}

	@PostMapping("/unscheduleJob")
	public ResponseEntity<Result<JobState>> unscheduleJob(@RequestBody JobKey jobKey) {
		JobState jobState = jobAdmin.unscheduleJob(jobKey);
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/scheduleJob")
	public ResponseEntity<Result<JobState>> scheduleJob(@RequestBody JobKey jobKey) {
		JobState jobState = jobAdmin.scheduleJob(jobKey);
		return ResponseEntity.ok(Result.success(jobState));
	}

}
