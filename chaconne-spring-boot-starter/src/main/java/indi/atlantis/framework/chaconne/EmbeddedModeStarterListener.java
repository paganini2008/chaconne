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
package indi.atlantis.framework.chaconne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.ArrayUtils;

import indi.atlantis.framework.chaconne.model.JobKeyQuery;
import indi.atlantis.framework.tridenter.election.ApplicationClusterRefreshedEvent;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * EmbeddedModeStarterListener
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class EmbeddedModeStarterListener implements ApplicationListener<ApplicationClusterRefreshedEvent> {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobListenerContainer jobListenerContainer;

	@Override
	public void onApplicationEvent(ApplicationClusterRefreshedEvent event) {
		handleParallelDependencies();
	}

	private void handleParallelDependencies() {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setClusterName(clusterName);
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		try {
			JobKey[] jobKeys = jobManager.getJobKeys(jobQuery);
			if (ArrayUtils.isNotEmpty(jobKeys)) {
				JobKey[] dependentKeys;
				for (JobKey jobKey : jobKeys) {
					// add listener to watch parallel dependency job done
					dependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.PARALLEL);
					if (ArrayUtils.isNotEmpty(dependentKeys)) {
						for (JobKey dependency : dependentKeys) {
							jobListenerContainer.addListener(dependency,
									ApplicationContextUtils.instantiateClass(ForkJoinJobListener.class));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
