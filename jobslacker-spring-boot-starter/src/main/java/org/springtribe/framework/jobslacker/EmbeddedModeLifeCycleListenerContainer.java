package org.springtribe.framework.jobslacker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springtribe.framework.cluster.multicast.ApplicationMulticastGroup;
import org.springtribe.framework.jobslacker.model.JobLifeCycleParam;

/**
 * 
 * EmbeddedModeLifeCycleListenerContainer
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class EmbeddedModeLifeCycleListenerContainer extends LifeCycleListenerContainer {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ApplicationMulticastGroup multicastGroup;

	@Override
	public void onChange(JobKey jobKey, JobLifeCycle lifeCycle) {
		multicastGroup.multicast(applicationName, LifeCycleListenerContainer.class.getName(),
				new JobLifeCycleParam(jobKey, lifeCycle));
	}

}
