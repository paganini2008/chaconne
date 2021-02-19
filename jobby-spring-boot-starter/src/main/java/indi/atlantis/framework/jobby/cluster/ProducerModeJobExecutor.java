package indi.atlantis.framework.jobby.cluster;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.jobby.Job;
import indi.atlantis.framework.jobby.JobException;
import indi.atlantis.framework.jobby.JobExecutor;
import indi.atlantis.framework.jobby.JobKey;
import indi.atlantis.framework.jobby.JobManager;
import indi.atlantis.framework.jobby.JobRuntimeListenerContainer;
import indi.atlantis.framework.jobby.JobState;
import indi.atlantis.framework.jobby.JobTemplate;
import indi.atlantis.framework.jobby.RunningState;
import indi.atlantis.framework.jobby.ScheduleManager;
import indi.atlantis.framework.jobby.StopWatch;
import indi.atlantis.framework.jobby.TraceIdGenerator;

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
