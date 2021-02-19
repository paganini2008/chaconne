package indi.atlantis.framework.jobby.cluster;

import java.util.Date;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.jobby.Job;
import indi.atlantis.framework.jobby.JobException;
import indi.atlantis.framework.jobby.JobExecutor;
import indi.atlantis.framework.jobby.JobKey;
import indi.atlantis.framework.jobby.JobManager;
import indi.atlantis.framework.jobby.JobRuntimeListenerContainer;
import indi.atlantis.framework.jobby.JobState;
import indi.atlantis.framework.jobby.JobTemplate;
import indi.atlantis.framework.jobby.RunningState;
import indi.atlantis.framework.jobby.SerialDependencyScheduler;
import indi.atlantis.framework.jobby.StopWatch;
import indi.atlantis.framework.jobby.TraceIdGenerator;
import indi.atlantis.framework.jobby.model.JobParam;
import indi.atlantis.framework.seafloor.Constants;
import indi.atlantis.framework.seafloor.multicast.ApplicationMulticastGroup;

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
