package indi.atlantis.framework.chaconne.model;

import com.github.paganini2008.devtools.beans.ToStringBuilder;

import indi.atlantis.framework.chaconne.JobKey;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobParam
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobParam {

	private JobKey jobKey;
	private Object attachment;
	private int retries;

	public JobParam() {
	}

	public JobParam(JobKey jobKey, Object attachment, int retries) {
		this.jobKey = jobKey;
		this.attachment = attachment;
		this.retries = retries;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
