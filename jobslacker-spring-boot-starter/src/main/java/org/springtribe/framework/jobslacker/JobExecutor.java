package org.springtribe.framework.jobslacker;

/**
 * 
 * JobExecutor
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface JobExecutor {

	void execute(Job job, Object attachment, int retries);

}
