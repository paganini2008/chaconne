package indi.atlantis.framework.chaconne.cluster;

import java.util.Date;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.chaconne.Job;
import indi.atlantis.framework.chaconne.JobException;
import indi.atlantis.framework.chaconne.JobExecutor;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.JobListenerContainer;
import indi.atlantis.framework.chaconne.JobState;
import indi.atlantis.framework.chaconne.JobTemplate;
import indi.atlantis.framework.chaconne.RunningState;
import indi.atlantis.framework.chaconne.SerialDependencyScheduler;
import indi.atlantis.framework.chaconne.StopWatch;
import indi.atlantis.framework.chaconne.TraceIdGenerator;
import indi.atlantis.framework.chaconne.model.JobParameter;
import indi.atlantis.framework.tridenter.Constants;
import indi.atlantis.framework.tridenter.multicast.ApplicationMulticastGroup;

/**
 * 
 * ConsumerModeLoadBalancer
 * 
 * @author Fred Feng
 *
 * @since 1.0
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
			final String topic = Constants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":scheduler:loadbalance";
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
