package indi.atlantis.framework.jobby.model;

import indi.atlantis.framework.jobby.JobKey;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobTraceQuery
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobTraceQuery extends Query {

	private JobKey jobKey;
	private long traceId;

	public JobTraceQuery() {
	}

	public JobTraceQuery(JobKey jobKey) {
		this.jobKey = jobKey;
	}

	public JobTraceQuery(JobKey jobKey, long traceId) {
		this.jobKey = jobKey;
		this.traceId = traceId;
	}

}
