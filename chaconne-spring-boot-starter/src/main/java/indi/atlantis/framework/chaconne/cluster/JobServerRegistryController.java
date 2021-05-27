package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.atlantis.framework.chaconne.model.JobResult;
import indi.atlantis.framework.tridenter.ApplicationInfo;

/**
 * 
 * JobServerRegistryController
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@RestController
@RequestMapping("/job/admin")
public class JobServerRegistryController {

	@Autowired
	private JobServerRegistry jobServerRegistry;

	@PostMapping("/registerCluster")
	public ResponseEntity<JobResult<Boolean>> registerCluster(@RequestBody ApplicationInfo applicationInfo) {
		jobServerRegistry.registerCluster(applicationInfo);
		return ResponseEntity.ok(JobResult.success(Boolean.TRUE));
	}

}
