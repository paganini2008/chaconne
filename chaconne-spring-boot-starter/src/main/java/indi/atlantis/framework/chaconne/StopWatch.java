package indi.atlantis.framework.chaconne;

import java.util.Date;

/**
 * 
 * StopWatch
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface StopWatch {

	JobState onJobBegin(long traceId, JobKey jobKey, Date startDate);

	JobState onJobEnd(long traceId, JobKey jobKey, Date startDate, RunningState runningState, int retries);

}
