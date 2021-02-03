package org.springtribe.framework.jobslacker.server;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.jobslacker.DependencyType;
import org.springtribe.framework.jobslacker.ExceptionUtils;
import org.springtribe.framework.jobslacker.Job;
import org.springtribe.framework.jobslacker.JobException;
import org.springtribe.framework.jobslacker.JobFuture;
import org.springtribe.framework.jobslacker.JobFutureHolder;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobManager;
import org.springtribe.framework.jobslacker.JobState;
import org.springtribe.framework.jobslacker.NoneJobFuture;
import org.springtribe.framework.jobslacker.ScheduleManager;
import org.springtribe.framework.jobslacker.Scheduler;
import org.springtribe.framework.jobslacker.TriggerType;
import org.springtribe.framework.jobslacker.model.JobDetail;
import org.springtribe.framework.jobslacker.model.JobTriggerDetail;
import org.springtribe.framework.jobslacker.model.TriggerDescription;
import org.springtribe.framework.jobslacker.model.TriggerDescription.Dependency;
import org.springtribe.framework.jobslacker.model.TriggerDescription.Periodic;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.date.DateUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ServerModeScheduleManager
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class ServerModeScheduleManager implements ScheduleManager {

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobFutureHolder jobFutureHolder;

	private final Observable jobTrigger = Observable.unrepeatable();

	@Override
	public JobState schedule(final Job job) throws Exception {
		final JobKey jobKey = JobKey.of(job);
		jobTrigger.addObserver((jobTrigger, ignored) -> {
			if (hasScheduled(jobKey)) {
				log.warn("Job '{}' has been scheduled.", jobKey);
				return;
			}
			JobDetail jobDetail;
			try {
				if (jobManager.hasJobState(jobKey, JobState.FINISHED)) {
					return;
				}
				jobDetail = jobManager.getJobDetail(jobKey, true);
			} catch (Exception e) {
				throw ExceptionUtils.wrapExeception(e);
			}
			JobFuture jobFuture = null;
			JobTriggerDetail triggerDetail = jobDetail.getJobTriggerDetail();
			if (triggerDetail.getTriggerType() == TriggerType.DEPENDENT) {
				if (triggerDetail.getTriggerDescriptionObject().getDependency().getDependencyType() == DependencyType.SERIAL) {
					log.info("Job '{}' will be triggered on client server.", jobKey);
				} else {
					jobFuture = scheduleDependency(jobKey, job, jobDetail.getAttachment(), triggerDetail);
				}
			} else {
				jobFuture = scheduleJob(jobKey, job, jobDetail.getAttachment(), triggerDetail);
			}
			if (jobFuture != null) {
				jobFutureHolder.add(jobKey, jobFuture);
				try {
					jobManager.setJobState(jobKey, JobState.SCHEDULING);
				} catch (Exception e) {
					throw ExceptionUtils.wrapExeception(e);
				}
				log.info("Schedule job '{}' ok. Current scheduling's number is {}", jobKey, countOfScheduling());
			}

		});
		return jobManager.getJobRuntime(jobKey).getJobState();
	}

	private JobFuture scheduleJob(JobKey jobKey, Job job, String attachment, JobTriggerDetail triggerDetail) {
		final TriggerDescription triggerDescription = triggerDetail.getTriggerDescriptionObject();
		Date startDate = triggerDetail.getStartDate();
		switch (triggerDetail.getTriggerType()) {
		case NONE:
			if (startDate != null) {
				return scheduler.schedule(job, attachment, startDate);
			} else {
				return new NoneJobFuture(jobKey);
			}
		case CRON:
			String cronExpression = triggerDescription.getCron().getExpression();
			if (startDate != null) {
				return scheduler.schedule(job, attachment, cronExpression, startDate);
			}
			return scheduler.schedule(job, attachment, cronExpression);
		case PERIODIC:
			Periodic periodic = triggerDescription.getPeriodic();
			long periodInMs = DateUtils.convertToMillis(periodic.getPeriod(), periodic.getSchedulingUnit().getTimeUnit());
			if (startDate == null) {
				startDate = new Date(System.currentTimeMillis() + periodInMs);
			}
			if (periodic.isFixedRate()) {
				return scheduler.scheduleAtFixedRate(job, attachment, periodInMs, startDate);
			}
			return scheduler.scheduleWithFixedDelay(job, attachment, periodInMs, startDate);
		default:
			break;
		}
		return null;
	}

	private JobFuture scheduleDependency(JobKey jobKey, Job job, String attachment, JobTriggerDetail triggerDetail) {
		final Dependency dependency = triggerDetail.getTriggerDescriptionObject().getDependency();
		Date startDate = triggerDetail.getStartDate();
		switch (dependency.getTriggerType()) {
		case NONE:
			if (startDate != null) {
				return scheduler.schedule(job, attachment, startDate);
			} else {
				return new NoneJobFuture(jobKey);
			}
		case CRON:
			String cronExpression = dependency.getCron().getExpression();
			if (startDate != null) {
				return scheduler.schedule(job, attachment, cronExpression, startDate);
			}
			return scheduler.schedule(job, attachment, cronExpression);
		case PERIODIC:
			Periodic periodic = dependency.getPeriodic();
			long periodInMs = DateUtils.convertToMillis(periodic.getPeriod(), periodic.getSchedulingUnit().getTimeUnit());
			if (startDate == null) {
				startDate = new Date(System.currentTimeMillis() + periodInMs);
			}
			if (periodic.isFixedRate()) {
				return scheduler.scheduleAtFixedRate(job, attachment, periodInMs, startDate);
			}
			return scheduler.scheduleWithFixedDelay(job, attachment, periodInMs, startDate);
		default:
			break;
		}
		return null;
	}

	@Override
	public JobState unscheduleJob(JobKey jobKey) throws Exception {
		if (hasScheduled(jobKey)) {
			jobFutureHolder.cancel(jobKey);
			log.info("Unschedule the job: " + jobKey);
			return jobManager.setJobState(jobKey, JobState.NOT_SCHEDULED);
		}
		return JobState.NOT_SCHEDULED;
	}

	@Override
	public boolean hasScheduled(JobKey jobKey) {
		return jobFutureHolder.hasKey(jobKey);
	}

	@Override
	public void doSchedule() {
		jobTrigger.notifyObservers();
		log.info("Do all schedules of jobs now.");
	}

	@Override
	public int countOfScheduling() {
		return jobFutureHolder.size();
	}

	@Override
	public JobFuture getJobFuture(JobKey jobKey) {
		if (!hasScheduled(jobKey)) {
			throw new JobException("Not scheduling job");
		}
		return jobFutureHolder.get(jobKey);
	}
}
