package indi.atlantis.framework.chaconne;

import java.util.Date;

/**
 * 
 * MailContentSource
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface MailContentSource {

	String getContent(long traceId, JobKey jobKey, Object attachment, Date startDate, RunningState runningState, Throwable reason);

	default boolean isHtml() {
		return false;
	}

}
