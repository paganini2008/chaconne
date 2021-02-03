package org.springtribe.framework.jobslacker.server;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.jobslacker.Job;
import org.springtribe.framework.jobslacker.JobException;
import org.springtribe.framework.jobslacker.JobExecutor;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobManager;
import org.springtribe.framework.jobslacker.JobRuntimeListenerContainer;
import org.springtribe.framework.jobslacker.JobState;
import org.springtribe.framework.jobslacker.JobTemplate;
import org.springtribe.framework.jobslacker.RunningState;
import org.springtribe.framework.jobslacker.ScheduleManager;
import org.springtribe.framework.jobslacker.StopWatch;
import org.springtribe.framework.jobslacker.TraceIdGenerator;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * ProducerModeJobExecutor
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ProducerModeJobExecutor extends JobTemplate implements JobExecutor {

	@Autowired
	private ScheduleManager scheduleManager;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private StopWatch stopWatch;

	@Autowired
	private TraceIdGenerator idGenerator;

	@Autowired
	private JobRuntimeListenerContainer jobRuntimeListenerContainer;

	@Override
	protected long getTraceId(JobKey jobKey) {
		return idGenerator.generateTraceId(jobKey);
	}

	@Override
	public void execute(Job job, Object attachment, int retries) {
		runJob(job, attachment, retries);
	}

	@Override
	protected void beforeRun(long traceId, JobKey jobKey, Job job, Object attachment, Date startDate) {
		super.beforeRun(traceId, jobKey, job, attachment, startDate);
		jobRuntimeListenerContainer.beforeRun(traceId, jobKey, job, attachment, startDate);
		stopWatch.onJobBegin(traceId, jobKey, startDate);
	}

	@Override
	protected boolean isScheduling(JobKey jobKey, Job job) {
		try {
			return jobManager.hasJobState(jobKey, JobState.SCHEDULING);
		} catch (Exception e) {
			throw new JobException(e.getMessage(), e);
		}
	}

	@Override
	protected void cancel(JobKey jobKey, Job job, RunningState runningState, String msg, Throwable reason) {
		try {
			scheduleManager.unscheduleJob(jobKey);
			jobManager.finishJob(jobKey);
		} catch (Exception e) {
			throw new JobException(e.getMessage(), e);
		}
		if (StringUtils.isNotBlank(msg)) {
			log.info(msg);
		}
		if (reason != null) {
			log.error(reason.getMessage(), reason);
		}
	}

}
