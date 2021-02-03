package org.springtribe.framework.jobslacker.model;

import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobLifeCycle;

import com.github.paganini2008.devtools.beans.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobLifeCycleParam
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobLifeCycleParam {

	private JobKey jobKey;
	private JobLifeCycle lifeCycle;

	public JobLifeCycleParam() {
	}

	public JobLifeCycleParam(JobKey jobKey, JobLifeCycle lifeCycle) {
		this.jobKey = jobKey;
		this.lifeCycle = lifeCycle;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
