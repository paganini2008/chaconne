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
package indi.atlantis.framework.chaconne;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.Observer;
import com.github.paganini2008.springdessert.reditools.messager.RedisMessageSender;

import indi.atlantis.framework.chaconne.model.JobParameter;
import indi.atlantis.framework.chaconne.model.JobResult;
import indi.atlantis.framework.tridenter.Constants;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SerialDependencySchedulerImpl
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class SerialDependencySchedulerImpl extends Observable implements SerialDependencyScheduler {

	public SerialDependencySchedulerImpl() {
		super(Boolean.TRUE);
	}

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Qualifier(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
	@Autowired
	private JobExecutor jobExecutor;

	@Autowired
	private JobFutureHolder JobFutureHolder;

	@Override
	public JobFuture scheduleDependency(final Job job, final JobKey... dependencies) {
		List<Observer> obs = new CopyOnWriteArrayList<Observer>();
		for (final JobKey dependency : dependencies) {
			Observer ob = (o, attachment) -> {
				final JobResult jobResult = (JobResult) attachment;
				if (approveIfJobCompleted(job, jobResult)) {
					jobExecutor.execute(job, jobResult.getResult(), 0);
				}
			};
			addObserver(dependency.getIdentifier(), ob);
			obs.add(ob);
			if (log.isTraceEnabled()) {
				log.trace("Add job dependency '{}' to job '{}'", dependency, job);
			}
		}
		return new SerialDependencyFuture(new CopyOnWriteArrayList<JobKey>(Arrays.asList(dependencies)), obs, this);
	}

	@Override
	public void updateDependency(final Job job, final JobKey... dependencies) {
		final JobKey jobKey = JobKey.of(job);
		JobFuture jobFuture = JobFutureHolder.get(jobKey);
		if (!(jobFuture instanceof SerialDependencyFuture)) {
			throw new JobException("Job '" + jobKey + "' is not a serial dependentcy job.");
		}
		SerialDependencyFuture jobDependencyFuture = (SerialDependencyFuture) jobFuture;
		for (JobKey dependency : dependencies) {
			if (jobDependencyFuture.getDependencies().contains(dependency)) {
				continue;
			}
			Observer ob = (o, attachment) -> {
				final JobResult jobResult = (JobResult) attachment;
				if (approveIfJobCompleted(job, jobResult)) {
					jobExecutor.execute(job, jobResult.getResult(), 0);
				}
			};
			addObserver(dependency.getIdentifier(), ob);

			jobDependencyFuture.getDependencies().add(dependency);
			jobDependencyFuture.getObservers().add(ob);
			if (log.isTraceEnabled()) {
				log.trace("Update job dependency '{}' to job '{}'", dependency, job);
			}
		}
	}

	@Override
	public boolean hasScheduled(JobKey jobKey) {
		return JobFutureHolder.get(jobKey) instanceof SerialDependencyFuture;
	}

	@Override
	public void triggerDependency(JobKey jobKey, Object attachment) {
		notifyObservers(jobKey.getIdentifier(), attachment);
	}

	@Override
	public void notifyDependants(JobKey jobKey, Object attachment) {
		final String channel = Constants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":scheduler:job:dependency:"
				+ jobKey.getIdentifier();
		JobParameter jobParam = new JobParameter(jobKey, attachment, 0);
		redisMessageSender.sendMessage(channel, jobParam);
		if (log.isTraceEnabled()) {
			log.trace("Immediately job '{}' is done and all serial dependencies will be notfied.", jobKey);
		}
	}

	private boolean approveIfJobCompleted(Job targetJob, JobResult jobResult) {
		if (ArrayUtils.isNotEmpty(targetJob.getDependencyPostHandlers())) {
			boolean result = true;
			for (Class<?> requiredType : targetJob.getDependencyPostHandlers()) {
				DependencyPostHandler handler = (DependencyPostHandler) ApplicationContextUtils.getOrCreateBean(requiredType);
				result &= handler.approve(jobResult.getJobKey(), jobResult.getRunningState(), jobResult.getAttachment(),
						jobResult.getResult());
			}
			return result;
		} else {
			return jobResult.getRunningState() == RunningState.COMPLETED;
		}

	}

}
