package org.springtribe.framework.jobslacker.model;

import java.util.Date;

import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.RunningState;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobRuntimeParam
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Getter
@Setter
@JsonInclude(value = Include.NON_NULL)
public class JobRuntimeParam {

	private long traceId;
	private JobKey jobKey;
	private Date startTime;
	private RunningState runningState;
	private int retries;

	public JobRuntimeParam(long traceId, JobKey jobKey, Date startTime, RunningState runningState, int retries) {
		this.traceId = traceId;
		this.jobKey = jobKey;
		this.startTime = startTime;
		this.runningState = runningState;
		this.retries = retries;
	}

	public JobRuntimeParam(long traceId, JobKey jobKey, Date startTime) {
		this.traceId = traceId;
		this.jobKey = jobKey;
		this.startTime = startTime;
	}

	public JobRuntimeParam() {
	}

}
