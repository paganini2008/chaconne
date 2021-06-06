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
import indi.atlantis.framework.chaconne.model.JobResult;
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
	public ResponseEntity<JobResult<Boolean>> registerCluster() throws Exception {
		ApplicationInfo applicationInfo = instanceId.getApplicationInfo();
		return restTemplate.perform(null, "/job/admin/registerCluster", HttpMethod.POST, applicationInfo,
				new ParameterizedTypeReference<JobResult<Boolean>>() {
				});
	}

	@PostMapping("/triggerJob")
	public ResponseEntity<JobResult<JobState>> triggerJob(@RequestBody JobParameter jobParam) throws Exception {
		JobState jobState = jobAdmin.triggerJob(jobParam.getJobKey(), jobParam.getAttachment());
		return ResponseEntity.ok(JobResult.success(jobState));
	}

	@PostMapping("/publicLifeCycleEvent")
	public ResponseEntity<JobResult<String>> publicLifeCycleEvent(@RequestBody JobLifeCycleParameter param) throws Exception {
		jobAdmin.publicLifeCycleEvent(param.getJobKey(), param.getLifeCycle());
		return ResponseEntity.ok(JobResult.success("ok"));
	}

}
