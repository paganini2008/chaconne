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

import java.util.Date;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.atlantisframework.chaconne.Job;
import io.atlantisframework.chaconne.JobException;
import io.atlantisframework.chaconne.JobExecutor;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobListenerContainer;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.JobState;
import io.atlantisframework.chaconne.JobTemplate;
import io.atlantisframework.chaconne.RunningState;
import io.atlantisframework.chaconne.SerialDependencyScheduler;
import io.atlantisframework.chaconne.StopWatch;
import io.atlantisframework.chaconne.TraceIdGenerator;
import io.atlantisframework.chaconne.model.JobParameter;
import io.atlantisframework.tridenter.ClusterConstants;
import io.atlantisframework.tridenter.multicast.ApplicationMulticastGroup;

/**
 * 
 * ConsumerModeLoadBalancer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class ConsumerModeLoadBalancer extends JobTemplate implements JobExecutor {

	@Autowired
	private ApplicationMulticastGroup applicationMulticastGroup;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private StopWatch stopWatch;

	@Autowired
	private SerialDependencyScheduler serialDependencyScheduler;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private JobListenerContainer jobListenerContainer;

	@Override
	protected long getTraceId(JobKey jobKey) {
		return TraceIdGenerator.NOOP.generateTraceId(jobKey);
	}

	@Override
	public void execute(Job job, Object attachment, int retries) {
		runJob(job, attachment, retries);
	}

	@Override
	protected void beforeRun(long traceId, JobKey jobKey, Job job, Object attachment, Date startDate) {
		super.beforeRun(traceId, jobKey, job, attachment, startDate);
		jobListenerContainer.beforeRun(traceId, jobKey, job, attachment, startDate);
		handleIfHasSerialDependency(traceId, jobKey, startDate);
	}

	private void handleIfHasSerialDependency(long traceId, JobKey jobKey, Date startDate) {
		if (serialDependencyScheduler.hasScheduled(jobKey)) {
			stopWatch.onJobBegin(traceId, jobKey, startDate);
		}
	}

	@Override
	protected final Object[] doRun(long traceId, JobKey jobKey, Job job, Object attachment, int retries, Logger log) {
		if (applicationMulticastGroup.countOfCandidate(jobKey.getGroupName()) > 0) {
			final String topic = ClusterConstants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":scheduler:loadbalance";
			applicationMulticastGroup.unicast(jobKey.getGroupName(), topic, new JobParameter(jobKey, attachment, retries));
		} else {
			try {
				jobManager.setJobState(jobKey, JobState.SCHEDULING);
			} catch (Exception e) {
				throw new JobException(e.getMessage(), e);
			}
		}
		return new Object[] { RunningState.RUNNING, null, null };
	}

	@Override
	protected boolean isScheduling(JobKey jobKey, Job job) {
		return true;
	}

}
