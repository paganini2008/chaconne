package indi.atlantis.framework.chaconne;

import java.util.Date;

/**
 * 
 * JobConditionalTermination
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public abstract class JobConditionalTermination implements JobListener {

	@Override
	public void beforeRun(long traceId, JobKey jobKey, Object attachment, Date startDate) {
		if (apply(traceId, jobKey, attachment, startDate)) {
			throw new JobTerminationException(jobKey, "Job '" + jobKey + "' has terminated on datetime: " + startDate);
		}
	}

	protected abstract boolean apply(long traceId, JobKey jobKey, Object attachment, Date startDate);

}
