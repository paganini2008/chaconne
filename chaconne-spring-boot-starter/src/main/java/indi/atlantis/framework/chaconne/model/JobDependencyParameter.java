package indi.atlantis.framework.chaconne.model;

import indi.atlantis.framework.chaconne.DependencyType;
import indi.atlantis.framework.chaconne.JobKey;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobDependencyParameter
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobDependencyParameter {

	private JobKey jobKey;
	private DependencyType dependencyType;

	public JobDependencyParameter() {
	}

	public JobDependencyParameter(JobKey jobKey, DependencyType dependencyType) {
		this.jobKey = jobKey;
		this.dependencyType = dependencyType;
	}

}
