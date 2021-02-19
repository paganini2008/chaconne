package indi.atlantis.framework.jobby;

import org.slf4j.Logger;

/**
 * 
 * RetryPolicy
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface RetryPolicy {

	Object retryIfNecessary(JobKey jobKey, Job job, Object attachment, Throwable reason, int retries, Logger log) throws Throwable;

}
