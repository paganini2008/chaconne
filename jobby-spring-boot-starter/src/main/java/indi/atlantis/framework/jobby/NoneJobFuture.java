package indi.atlantis.framework.jobby;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import indi.atlantis.framework.jobby.model.JobDetail;
import indi.atlantis.framework.jobby.model.JobRuntime;
import indi.atlantis.framework.jobby.model.JobTriggerDetail;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * NoneJobFuture
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class NoneJobFuture implements JobFuture {

	private final JobKey jobKey;

	public NoneJobFuture(JobKey jobKey) {
		this.jobKey = jobKey;
	}

	private final AtomicBoolean done = new AtomicBoolean();
	private final AtomicBoolean cancel = new AtomicBoolean();

	@Override
	public void cancel() {
		done.set(true);
		cancel.set(true);
	}

	@Override
	public boolean isDone() {
		return done.get();
	}

	@Override
	public boolean isCancelled() {
		return cancel.get();
	}

	@Override
	public long getNextExectionTime(Date lastExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
		JobManager jobManager = ApplicationContextUtils.getBean(JobManager.class);
		JobDetail jobDetail;
		try {
			jobDetail = jobManager.getJobDetail(jobKey, true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return NEXT_EXECUTION_TIME_NOT_FOUND;
		}
		JobTriggerDetail triggerDetail = jobDetail.getJobTriggerDetail();
		if (triggerDetail.getTriggerType() == TriggerType.DEPENDENT) {
			if (triggerDetail.getTriggerDescriptionObject().getDependency().getDependencyType() == DependencyType.PARALLEL) {
				JobRuntime jobRuntime;
				try {
					JobKey relation = jobManager.getRelations(jobKey, DependencyType.PARALLEL)[0];
					jobRuntime = jobManager.getJobRuntime(relation);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					return NEXT_EXECUTION_TIME_NOT_FOUND;
				}
				return jobRuntime.getNextExecutionTime().getTime();
			}
		}
		return NEXT_EXECUTION_TIME_NOT_FOUND;
	}

}
