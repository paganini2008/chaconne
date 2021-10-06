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

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.multithreads.RetryableTimer;

import io.atlantisframework.chaconne.DependencyType;
import io.atlantisframework.chaconne.ForkJoinJobListener;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobListenerContainer;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.TriggerType;
import io.atlantisframework.chaconne.model.JobKeyQuery;
import io.atlantisframework.chaconne.model.Result;
import io.atlantisframework.tridenter.ApplicationInfo;
import io.atlantisframework.tridenter.InstanceId;
import io.atlantisframework.tridenter.election.ApplicationClusterRefreshedEvent;
import io.atlantisframework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ConsumerModeStarterListener
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class ConsumerModeStarterListener extends RestClientRetryable
		implements ApplicationListener<ApplicationClusterRefreshedEvent>, Ordered {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private InstanceId instanceId;

	@Autowired
	private ConsumerModeRestTemplate restTemplate;

	@Autowired
	private JobListenerContainer jobListenerContainer;

	@Autowired
	private RetryableTimer retryableTimer;

	@Override
	public void onApplicationEvent(ApplicationClusterRefreshedEvent event) {
		retryableTimer.executeAndRetryWithFixedDelay(this, DEFAULT_RETRY_INTERVAL, TimeUnit.SECONDS);
	}

	@Override
	public void execute() throws Throwable {
		registerJobAdmin();
		handleParallelDependencies();
	}

	private void registerJobAdmin() throws Exception {
		final ApplicationInfo applicationInfo = instanceId.getApplicationInfo();
		ResponseEntity<Result<Boolean>> responseEntity = restTemplate.perform("", "/job/admin/registerJobExecutor", HttpMethod.POST,
				applicationInfo, new ParameterizedTypeReference<Result<Boolean>>() {
				});
		if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody().getData()) {
			log.info("'{}' register to job admin ok.", applicationInfo);
		}
	}

	private void handleParallelDependencies() throws Exception {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setClusterName(clusterName);
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		JobKey[] jobKeys = jobManager.getJobKeys(jobQuery);
		if (ArrayUtils.isNotEmpty(jobKeys)) {
			JobKey[] dependentKeys;
			for (JobKey jobKey : jobKeys) {
				// add listener to watch parallel dependency job done
				dependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.PARALLEL);
				if (ArrayUtils.isNotEmpty(dependentKeys)) {
					for (JobKey dependency : dependentKeys) {
						jobListenerContainer.addListener(dependency, ApplicationContextUtils.instantiateClass(ForkJoinJobListener.class));
					}
				}
			}
		}
	}

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE - 200;
	}

}
