package indi.atlantis.framework.chaconne;

import java.util.Date;

/**
 * 
 * JobListener
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface JobListener extends Comparable<JobListener> {

	default void beforeRun(long traceId, JobKey jobKey, Object attachment, Date startDate) {
	}

	default void afterRun(long traceId, JobKey jobKey, Object attachment, Date startDate, RunningState runningState, Object result,
			Throwable reason) {
	}

	default int getOrder() {
		return 0;
	}

	default int compareTo(JobListener other) {
		return other.getOrder() - getOrder();
	}

}
