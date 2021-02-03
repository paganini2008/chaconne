package org.springtribe.framework.jobslacker;

import java.util.Date;

/**
 * 
 * MailContentSource
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface MailContentSource {

	String getContent(long traceId, JobKey jobKey, Object attachment, Date startDate, RunningState runningState, Throwable reason);

	default boolean isHtml() {
		return false;
	}

}
