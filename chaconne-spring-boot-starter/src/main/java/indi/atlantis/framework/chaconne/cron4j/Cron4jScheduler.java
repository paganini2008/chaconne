/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package indi.atlantis.framework.chaconne.cron4j;

import java.util.Date;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.ErrorHandler;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.cron4j.CRON;
import com.github.paganini2008.devtools.cron4j.Task;
import com.github.paganini2008.devtools.cron4j.TaskExecutor;
import com.github.paganini2008.devtools.cron4j.TaskExecutor.TaskFuture;

import indi.atlantis.framework.chaconne.ChaconneBeanNames;
import indi.atlantis.framework.chaconne.Job;
import indi.atlantis.framework.chaconne.JobExecutor;
import indi.atlantis.framework.chaconne.JobFuture;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.Scheduler;
import indi.atlantis.framework.chaconne.SerialDependencyScheduler;

/**
 * 
 * Cron4jScheduler
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class Cron4jScheduler implements Scheduler {

	@Qualifier(ChaconneBeanNames.JOB_SCHEDULER)
	@Autowired
	private TaskExecutor taskExecutor;

	@Qualifier(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
	@Autowired
	private JobExecutor jobExecutor;

	@Qualifier(ChaconneBeanNames.SCHEDULER_ERROR_HANDLER)
	@Autowired
	private ErrorHandler errorHandler;

	@Autowired
	private SerialDependencyScheduler serialDependencyScheduler;

	@Override
	public JobFuture schedule(Job job, Object attachment, Date startDate) {
		TaskFuture taskFuture = taskExecutor.schedule(new SimpleTask(job, attachment), startDate.getTime() - System.currentTimeMillis());
		return new JobFutureImpl(taskFuture);
	}

	@Override
	public JobFuture schedule(Job job, Object attachment, String cronExpression) {
		TaskFuture taskFuture = taskExecutor.schedule(new SimpleTask(job, attachment), CRON.parse(cronExpression));
		return new JobFutureImpl(taskFuture);
	}

	@Override
	public JobFuture schedule(Job job, Object attachment, String cronExpression, Date startDate) {
		return schedule(() -> {
			return schedule(job, attachment, cronExpression);
		}, startDate);
	}

	@Override
	public JobFuture scheduleWithFixedDelay(Job job, Object attachment, long period, Date startDate) {
		TaskFuture taskFuture = taskExecutor.scheduleWithFixedDelay(new SimpleTask(job, attachment),
				startDate.getTime() - System.currentTimeMillis(), period);
		return new JobFutureImpl(taskFuture);
	}

	@Override
	public JobFuture scheduleAtFixedRate(Job job, Object attachment, long period, Date startDate) {
		TaskFuture taskFuture = taskExecutor.scheduleAtFixedRate(new SimpleTask(job, attachment),
				startDate.getTime() - System.currentTimeMillis(), period);
		return new JobFutureImpl(taskFuture);
	}

	@Override
	public JobFuture scheduleWithDependency(Job job, JobKey[] dependencies) {
		return serialDependencyScheduler.scheduleDependency(job, dependencies);
	}

	@Override
	public JobFuture scheduleWithDependency(Job job, JobKey[] dependencies, Date startDate) {
		return schedule(() -> {
			return serialDependencyScheduler.scheduleDependency(job, dependencies);
		}, startDate);
	}

	private JobFuture schedule(Supplier<JobFuture> supplier, Date startDate) {
		final Observable canceller = Observable.unrepeatable();
		final TaskFuture taskFuture = taskExecutor.schedule(new Task() {
			@Override
			public boolean execute() {
				final JobFuture target = supplier.get();
				canceller.addObserver((ob, arg) -> {
					target.cancel();
				});
				return true;
			}
		}, startDate.getTime() - System.currentTimeMillis());
		return new DelayedJobFuture(taskFuture, canceller);
	}

	@Override
	public void runJob(Job job, Object attachment) {
		jobExecutor.execute(job, attachment, 0);
	}

	private class SimpleTask implements Task {

		private final Job job;
		private final Object attachment;

		SimpleTask(Job job, Object attachment) {
			this.job = job;
			this.attachment = attachment;
		}

		@Override
		public boolean execute() {
			runJob(job, attachment);
			return true;
		}

		@Override
		public boolean onError(Throwable cause) {
			errorHandler.handleError(cause);
			return true;
		}
	}

	/**
	 * 
	 * JobFutureImpl
	 * 
	 * @author Fred Feng
	 *
	 * @since 1.0
	 */
	private static class JobFutureImpl implements JobFuture {

		private final TaskFuture taskFuture;

		JobFutureImpl(TaskFuture taskFuture) {
			this.taskFuture = taskFuture;
		}

		@Override
		public void cancel() {
			taskFuture.cancel();
		}

		@Override
		public boolean isDone() {
			return taskFuture.isDone();
		}

		@Override
		public boolean isCancelled() {
			return taskFuture.isCancelled();
		}

		@Override
		public long getNextExectionTime(Date lastExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
			return taskFuture.getDetail().nextExectionTime();
		}

	}

	/**
	 * 
	 * DelayedJobFuture
	 * 
	 * @author Fred Feng
	 *
	 * @since 1.0
	 */
	private static class DelayedJobFuture extends JobFutureImpl {

		private final Observable canceller;

		DelayedJobFuture(TaskFuture taskFuture, Observable canceller) {
			super(taskFuture);
			this.canceller = canceller;
		}

		@Override
		public void cancel() {
			super.cancel();
			canceller.notifyObservers();
		}

	}

}
