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

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import indi.atlantis.framework.chaconne.model.JobKeyQuery;
import indi.atlantis.framework.chaconne.model.JobTriggerDetail;
import indi.atlantis.framework.chaconne.model.TriggerDescription.Dependency;
import indi.atlantis.framework.tridenter.LeaderState;
import indi.atlantis.framework.tridenter.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobDependencyUpdater
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class JobDependencyUpdater implements ApplicationListener<ApplicationClusterLeaderEvent>, Executable, BeanLifeCycle {

	private Timer timer;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobDao jobDao;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Override
	public boolean execute() {
		refresh();
		return true;
	}

	protected JobKey[] selectDependentKeys() throws Exception {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setClusterName(clusterName);
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		return jobManager.getJobKeys(jobQuery);
	}

	private void refresh() {
		JobKey[] jobKeys;
		try {
			jobKeys = selectDependentKeys();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return;
		}

		Map<JobKey, JobKey[]> serialDependencies = new HashMap<JobKey, JobKey[]>();
		Map<JobKey, JobKey[]> parallelDependencies = new HashMap<JobKey, JobKey[]>();
		JobKey[] dependentKeys;
		JobKey[] comparedDependentKeys;
		JobKey[] requiredJobKeys;
		JobTriggerDetail triggerDetail;
		Dependency dependency;
		for (JobKey jobKey : jobKeys) {
			try {
				triggerDetail = jobManager.getJobTriggerDetail(jobKey);
				dependency = triggerDetail.getTriggerDescriptionObject().getDependency();

				switch (dependency.getDependencyType()) {
				case SERIAL:
					dependentKeys = dependency.getDependentKeys();
					comparedDependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.SERIAL);
					requiredJobKeys = ArrayUtils.minus(dependentKeys, comparedDependentKeys);
					if (ArrayUtils.isNotEmpty(requiredJobKeys)) {
						serialDependencies.put(jobKey, requiredJobKeys);
					}
					break;
				case PARALLEL:
					dependentKeys = dependency.getForkKeys();
					comparedDependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.PARALLEL);
					requiredJobKeys = ArrayUtils.minus(dependentKeys, comparedDependentKeys);
					if (ArrayUtils.isNotEmpty(requiredJobKeys)) {
						parallelDependencies.put(jobKey, requiredJobKeys);
					}
					break;
				case MIXED:
					dependentKeys = dependency.getDependentKeys();
					comparedDependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.SERIAL);
					requiredJobKeys = ArrayUtils.minus(dependentKeys, comparedDependentKeys);
					if (ArrayUtils.isNotEmpty(requiredJobKeys)) {
						serialDependencies.put(jobKey, requiredJobKeys);
					}

					dependentKeys = dependency.getForkKeys();
					comparedDependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.PARALLEL);
					requiredJobKeys = ArrayUtils.minus(dependentKeys, comparedDependentKeys);
					if (ArrayUtils.isNotEmpty(requiredJobKeys)) {
						parallelDependencies.put(jobKey, requiredJobKeys);
					}
					break;
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		for (Map.Entry<JobKey, JobKey[]> entry : serialDependencies.entrySet()) {
			JobKey jobKey = entry.getKey();
			JobKey[] keys = entry.getValue();
			if (ArrayUtils.isNotEmpty(keys)) {
				for (JobKey key : keys) {
					try {
						if (jobManager.hasJob(key)) {
							saveJobDependency(jobKey, key, DependencyType.SERIAL);
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}

		for (Map.Entry<JobKey, JobKey[]> entry : parallelDependencies.entrySet()) {
			JobKey jobKey = entry.getKey();
			JobKey[] keys = entry.getValue();
			if (ArrayUtils.isNotEmpty(keys)) {
				for (JobKey key : keys) {
					try {
						if (jobManager.hasJob(key)) {
							saveJobDependency(jobKey, key, DependencyType.PARALLEL);
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}

	}

	private void saveJobDependency(JobKey jobKey, JobKey dependency, DependencyType dependencyType) throws Exception {
		final int jobId = jobManager.getJobId(jobKey);
		final int dependentId = jobManager.getJobId(dependency);
		Map<String, Object> kwargs = new HashMap<String, Object>();
		kwargs.put("jobId", jobId);
		kwargs.put("dependentJobId", dependentId);
		kwargs.put("dependencyType", dependencyType.getValue());
		jobDao.saveJobDependency(kwargs);
		log.info("Add job dependency '{}' to jobId {} ok.", dependency, jobId);
	}

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		if (event.getLeaderState() == LeaderState.UP) {
			this.timer = ThreadUtils.scheduleWithFixedDelay(this, 1, TimeUnit.MINUTES);
		}
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
