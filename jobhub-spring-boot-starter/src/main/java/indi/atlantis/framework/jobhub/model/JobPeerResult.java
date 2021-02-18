package indi.atlantis.framework.jobhub.model;

import indi.atlantis.framework.jobhub.JobKey;
import indi.atlantis.framework.jobhub.RunningState;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobPeerResult
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobPeerResult {

	private JobKey jobKey;
	private Object attachment;
	private RunningState runningState;
	private Object result;

	public JobPeerResult() {
	}

	public JobPeerResult(JobKey jobKey, Object attachment, RunningState runningState, Object result) {
		this.jobKey = jobKey;
		this.attachment = attachment;
		this.runningState = runningState;
		this.result = result;
	}

}
