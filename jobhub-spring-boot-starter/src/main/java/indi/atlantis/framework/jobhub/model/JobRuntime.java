package indi.atlantis.framework.jobhub.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.paganini2008.devtools.beans.ToStringBuilder;

import indi.atlantis.framework.jobhub.JobState;
import indi.atlantis.framework.jobhub.RunningState;
import lombok.Getter;

/**
 * 
 * JobRuntime
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
@JsonInclude(value = Include.NON_NULL)
@Getter
public class JobRuntime implements Serializable {

	private static final long serialVersionUID = -6283587791317006889L;
	private int jobId;
	private JobState jobState;
	private RunningState lastRunningState;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastExecutionTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastCompletionTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date nextExecutionTime;

	public JobRuntime() {
	}

	public void setJobState(int jobState) {
		this.jobState = JobState.valueOf(jobState);
	}

	public void setLastRunningState(int runningState) {
		this.lastRunningState = RunningState.valueOf(runningState);
	}

	public void setLastExecutionTime(Date lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
	}

	public void setLastCompletionTime(Date lastCompletionTime) {
		this.lastCompletionTime = lastCompletionTime;
	}

	public void setNextExecutionTime(Date nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
