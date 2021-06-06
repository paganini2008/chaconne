package indi.atlantis.framework.chaconne.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.RunningState;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobRuntimeParameter
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
@JsonInclude(value = Include.NON_NULL)
public class JobRuntimeParameter {

	private long traceId;
	private JobKey jobKey;
	private Date startTime;
	private RunningState runningState;
	private int retries;

	public JobRuntimeParameter(long traceId, JobKey jobKey, Date startTime, RunningState runningState, int retries) {
		this.traceId = traceId;
		this.jobKey = jobKey;
		this.startTime = startTime;
		this.runningState = runningState;
		this.retries = retries;
	}

	public JobRuntimeParameter(long traceId, JobKey jobKey, Date startTime) {
		this.traceId = traceId;
		this.jobKey = jobKey;
		this.startTime = startTime;
	}

	public JobRuntimeParameter() {
	}

}
