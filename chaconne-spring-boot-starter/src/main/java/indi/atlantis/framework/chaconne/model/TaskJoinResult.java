package indi.atlantis.framework.chaconne.model;

import indi.atlantis.framework.chaconne.JobKey;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * TaskJoinResult
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@Getter
@Setter
public class TaskJoinResult {

	private JobKey jobKey;
	private Object attachment;
	private TaskForkResult[] taskForkResults;

	public TaskJoinResult() {
	}

	public TaskJoinResult(JobKey jobKey, Object attachment, TaskForkResult[] taskForkResults) {
		this.jobKey = jobKey;
		this.attachment = attachment;
		this.taskForkResults = taskForkResults;
	}

}
