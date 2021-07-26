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

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;

import indi.atlantis.framework.chaconne.model.JobKeyQuery;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * EmbeddedModeJobBeanInitializer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class EmbeddedModeJobBeanInitializer implements JobBeanInitializer {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobQueryDao jobQueryDao;

	@Autowired
	private ScheduleManager scheduleManager;

	@Qualifier(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
	@Autowired
	private JobBeanLoader internalJobBeanLoader;

	@Qualifier(ChaconneBeanNames.EXTERNAL_JOB_BEAN_LOADER)
	@Autowired(required = false)
	private JobBeanLoader externalJobBeanLoader;

	@Autowired
	private JobListenerContainer jobListenerContainer;

	public void initializeJobBeans() throws Exception {
		refreshInternalJobBeans();
		if (externalJobBeanLoader != null) {
			refreshExternalJobBeans();
		}
		initializeJobDependencyBeans();
	}

	private void refreshInternalJobBeans() throws Exception {
		List<Map<String, Object>> dataList = jobQueryDao.selectJobDetailsByGroupName(clusterName, applicationName);
		if (CollectionUtils.isNotEmpty(dataList)) {
			JobKey jobKey;
			Job job;
			for (Map<String, Object> data : dataList) {
				jobKey = JobKey.of(data);
				try {
					job = internalJobBeanLoader.loadJobBean(jobKey);
				} catch (Exception e) {
					log.error("Unable to load Job Bean: {}", jobKey, e);
					continue;
				}
				if (job == null) {
					continue;
				}
				if (scheduleManager.hasScheduled(jobKey)) {
					continue;
				}
				scheduleManager.schedule(job);
				log.info("Reload and schedule Job '{}' ok.", jobKey);
			}
		}
	}

	private void refreshExternalJobBeans() throws Exception {
		List<Map<String, Object>> dataList = jobQueryDao.selectJobDetailsByOtherGroupName(clusterName, applicationName);
		if (CollectionUtils.isNotEmpty(dataList)) {
			JobKey jobKey;
			Job job;
			for (Map<String, Object> data : dataList) {
				jobKey = JobKey.of(data);
				try {
					job = externalJobBeanLoader.loadJobBean(jobKey);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					continue;
				}
				if (job == null) {
					continue;
				}
				if (scheduleManager.hasScheduled(jobKey)) {
					continue;
				}
				scheduleManager.schedule(job);
				log.info("Reload and schedule Job '{}' ok.", jobKey);
			}
		}
	}

	private void initializeJobDependencyBeans() throws Exception {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setClusterName(clusterName);
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		JobKey[] jobKeys = jobManager.getJobKeys(jobQuery);
		if (ArrayUtils.isNotEmpty(jobKeys)) {
			JobKey[] dependencies;
			for (JobKey jobKey : jobKeys) {
				// add listener to watch parallel dependency job done
				dependencies = jobManager.getDependentKeys(jobKey, DependencyType.PARALLEL);
				if (ArrayUtils.isNotEmpty(dependencies)) {
					for (JobKey dependency : dependencies) {
						jobListenerContainer.addListener(dependency,
								ApplicationContextUtils.instantiateClass(ForkJoinJobListener.class));
					}
				}
			}
		}
	}

}
