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

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.multithreads.RetryableTimer;

import io.atlantisframework.chaconne.ChaconneBeanNames;
import io.atlantisframework.chaconne.DependencyType;
import io.atlantisframework.chaconne.Job;
import io.atlantisframework.chaconne.JobBeanInitializer;
import io.atlantisframework.chaconne.JobBeanLoader;
import io.atlantisframework.chaconne.JobFutureHolder;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.SerialDependencyScheduler;
import io.atlantisframework.chaconne.TriggerType;
import io.atlantisframework.chaconne.model.JobKeyQuery;

/**
 * 
 * ConsumerModeJobBeanInitializer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class ConsumerModeJobBeanInitializer extends RestClientRetryable implements JobBeanInitializer {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private JobManager jobManager;

	@Qualifier(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
	@Autowired
	private JobBeanLoader jobBeanLoader;

	@Qualifier(ChaconneBeanNames.EXTERNAL_JOB_BEAN_LOADER)
	@Autowired(required = false)
	private JobBeanLoader externalJobBeanLoader;

	@Autowired
	private SerialDependencyScheduler serialDependencyScheduler;

	@Autowired
	private JobFutureHolder jobFutureHolder;

	@Autowired
	private RetryableTimer retryableTimer;

	@Override
	public void initializeJobBeans() throws Exception {
		retryableTimer.executeAndRetryWithFixedDelay(this, DEFAULT_RETRY_INTERVAL, TimeUnit.SECONDS);
	}

	@Override
	public void execute() throws Throwable {
		handleSerialDependencies();
	}

	private void handleSerialDependencies() throws Exception {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setClusterName(clusterName);
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		JobKey[] jobKeys = jobManager.getJobKeys(jobQuery);
		if (ArrayUtils.isNotEmpty(jobKeys)) {
			Job job;
			JobKey[] dependentKeys;
			for (JobKey jobKey : jobKeys) {
				job = jobBeanLoader.loadJobBean(jobKey);
				if (job == null && externalJobBeanLoader != null) {
					job = externalJobBeanLoader.loadJobBean(jobKey);
				}
				if (job == null) {
					continue;
				}
				// update or schedule serial dependency job
				dependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.SERIAL);
				if (ArrayUtils.isNotEmpty(dependentKeys)) {
					if (serialDependencyScheduler.hasScheduled(jobKey)) {
						serialDependencyScheduler.updateDependency(job, dependentKeys);
					} else {
						jobFutureHolder.add(jobKey, serialDependencyScheduler.scheduleDependency(job, dependentKeys));
					}
				}

			}
		}
	}

}
