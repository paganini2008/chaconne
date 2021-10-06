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
package io.atlantisframework.chaconne;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.date.DateUtils;

import io.atlantisframework.chaconne.model.JobDetail;
import io.atlantisframework.chaconne.model.JobTriggerDetail;
import io.atlantisframework.chaconne.model.TriggerDescription;
import io.atlantisframework.chaconne.model.TriggerDescription.Dependency;
import io.atlantisframework.chaconne.model.TriggerDescription.Periodic;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DefaultScheduleManager
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class DefaultScheduleManager implements ScheduleManager {

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
				log.warn("Job '{}' is being scheduled.", jobKey);
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

			JobFuture jobFuture;
			JobTriggerDetail triggerDetail = jobDetail.getJobTriggerDetail();
			if (triggerDetail.getTriggerType() == TriggerType.DEPENDENT) {
				jobFuture = scheduleDependency(jobKey, job, jobDetail.getAttachment(), triggerDetail);
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
		return jobManager.getJobRuntimeDetail(jobKey).getJobState();
	}

	private JobFuture scheduleDependency(JobKey jobKey, Job job, String attachment, JobTriggerDetail triggerDetail) {
		final Dependency dependency = triggerDetail.getTriggerDescriptionObject().getDependency();
		Date startDate = triggerDetail.getStartDate();
		if (startDate != null && startDate.before(new Date())) {
			log.warn("StartDate is past and reset to null. JobKey: {}", jobKey);
			startDate = null;
		}
		if (dependency.getDependencyType() == DependencyType.SERIAL || dependency.getDependencyType() == DependencyType.MIXED) {
			if (startDate != null) {
				return scheduler.scheduleWithDependency(job, dependency.getDependentKeys(), startDate);
			}
			return scheduler.scheduleWithDependency(job, dependency.getDependentKeys());
		} else if (dependency.getDependencyType() == DependencyType.PARALLEL) {
			switch (dependency.getTriggerType()) {
			case SIMPLE:
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
		}
		return null;
	}

	private JobFuture scheduleJob(JobKey jobKey, Job job, String attachment, JobTriggerDetail triggerDetail) {
		final TriggerDescription triggerDescription = triggerDetail.getTriggerDescriptionObject();
		Date startDate = triggerDetail.getStartDate();
		if (startDate != null && startDate.before(new Date())) {
			log.warn("StartDate is past and reset to null. JobKey: {}", jobKey);
			startDate = null;
		}
		switch (triggerDetail.getTriggerType()) {
		case SIMPLE:
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
	public synchronized void doSchedule() {
		jobTrigger.notifyObservers();
		log.info("Do all job schedules now.");
	}

	@Override
	public int countOfScheduling() {
		return jobFutureHolder.size();
	}

	@Override
	public JobFuture getJobFuture(JobKey jobKey) {
		if (!hasScheduled(jobKey)) {
			throw new JobException("Not scheduling job by key: " + jobKey);
		}
		return jobFutureHolder.get(jobKey);
	}

}
