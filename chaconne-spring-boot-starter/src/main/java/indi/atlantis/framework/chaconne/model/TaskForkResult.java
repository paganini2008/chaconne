package indi.atlantis.framework.chaconne.model;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.RunningState;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * TaskForkResult
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Getter
@Setter
public class TaskForkResult {

	private JobKey jobKey;
	private Object attachment;
	private RunningState runningState;
	private Object result;

	public TaskForkResult() {
	}

	public TaskForkResult(JobKey jobKey, Object attachment, RunningState runningState, Object result) {
		this.jobKey = jobKey;
		this.attachment = attachment;
		this.runningState = runningState;
		this.result = result;
	}

}
