package indi.atlantis.framework.chaconne.model;

import java.util.Date;

import indi.atlantis.framework.chaconne.JobKey;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobTracePageQuery
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobTracePageQuery<T> extends PageQuery<T> {

	private JobKey jobKey;
	private Date startDate;
	private Date endDate;
	
	public JobTracePageQuery() {
	}

	public JobTracePageQuery(JobKey jobKey) {
		this.jobKey = jobKey;
	}

	public JobTracePageQuery(JobKey jobKey, int page, int size) {
		super(page, size);
		this.jobKey = jobKey;
	}

}
