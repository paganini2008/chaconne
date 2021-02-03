package org.springtribe.framework.jobslacker;

import java.util.Date;

/**
 * 
 * JobFuture
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface JobFuture {

	static final long NEXT_EXECUTION_TIME_NOT_FOUND = -1L;

	void cancel();

	boolean isDone();

	boolean isCancelled();

	long getNextExectionTime(Date lastExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime);

}
