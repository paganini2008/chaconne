package indi.atlantis.framework.chaconne.model;

import com.github.paganini2008.devtools.beans.ToStringBuilder;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobLifeCycle;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobLifeCycleParameter
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobLifeCycleParameter {

	private JobKey jobKey;
	private JobLifeCycle lifeCycle;

	public JobLifeCycleParameter() {
	}

	public JobLifeCycleParameter(JobKey jobKey, JobLifeCycle lifeCycle) {
		this.jobKey = jobKey;
		this.lifeCycle = lifeCycle;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
