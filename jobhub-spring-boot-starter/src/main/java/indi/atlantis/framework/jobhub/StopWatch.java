package indi.atlantis.framework.jobhub;

import java.util.Date;

/**
 * 
 * StopWatch
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface StopWatch {

	JobState onJobBegin(long traceId, JobKey jobKey, Date startDate);

	JobState onJobEnd(long traceId, JobKey jobKey, Date startDate, RunningState runningState, int retries);

}
