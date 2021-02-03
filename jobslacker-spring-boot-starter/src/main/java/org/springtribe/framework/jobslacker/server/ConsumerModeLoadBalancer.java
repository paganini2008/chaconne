package org.springtribe.framework.jobslacker.server;

import java.util.Date;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springtribe.framework.cluster.Constants;
import org.springtribe.framework.cluster.multicast.ApplicationMulticastGroup;
import org.springtribe.framework.jobslacker.Job;
import org.springtribe.framework.jobslacker.JobException;
import org.springtribe.framework.jobslacker.JobExecutor;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobManager;
import org.springtribe.framework.jobslacker.JobRuntimeListenerContainer;
import org.springtribe.framework.jobslacker.JobState;
import org.springtribe.framework.jobslacker.JobTemplate;
import org.springtribe.framework.jobslacker.RunningState;
import org.springtribe.framework.jobslacker.SerialDependencyScheduler;
import org.springtribe.framework.jobslacker.StopWatch;
import org.springtribe.framework.jobslacker.TraceIdGenerator;
import org.springtribe.framework.jobslacker.model.JobParam;

/**
 * 
 * ConsumerModeLoadBalancer
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ConsumerModeLoadBalancer extends JobTemplate implements JobExecutor {

	@Autowired
	private ApplicationMulticastGroup multicastGroup;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private StopWatch stopWatch;

	@Autowired
	private SerialDependencyScheduler serialDependencyScheduler;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private JobRuntimeListenerContainer jobRuntimeListenerContainer;

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
		jobRuntimeListenerContainer.beforeRun(traceId, jobKey, job, attachment, startDate);
		handleIfHasSerialDependency(traceId, jobKey, startDate);
	}

	private void handleIfHasSerialDependency(long traceId, JobKey jobKey, Date startDate) {
		if (serialDependencyScheduler.hasScheduled(jobKey)) {
			stopWatch.onJobBegin(traceId, jobKey, startDate);
		}
	}

	@Override
	protected final Object[] doRun(long traceId, JobKey jobKey, Job job, Object attachment, int retries, Logger log) {
		if (multicastGroup.countOfCandidate(jobKey.getGroupName()) > 0) {
			final String topic = Constants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":scheduler:loadbalance";
			multicastGroup.unicast(jobKey.getGroupName(), topic, new JobParam(jobKey, attachment, retries));
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
