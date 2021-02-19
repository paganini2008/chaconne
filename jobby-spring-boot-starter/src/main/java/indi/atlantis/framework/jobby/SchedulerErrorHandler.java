package indi.atlantis.framework.jobby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ErrorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SchedulingErrorHandler
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class SchedulerErrorHandler implements ErrorHandler {

	@Autowired
	private ScheduleManager scheduleManager;

	@Autowired
	private JobManager jobManager;

	@Override
	public void handleError(Throwable t) {
		if (t instanceof JobTerminationException) {
			final JobTerminationException cause = (JobTerminationException) t;
			final JobKey jobKey = cause.getJobKey();
			try {
				scheduleManager.unscheduleJob(jobKey);
				jobManager.finishJob(jobKey);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.error(t.getMessage(), t);
		}
	}

}
