package indi.atlantis.framework.chaconne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.chaconne.model.JobLifeCycleParameter;
import indi.atlantis.framework.tridenter.multicast.ApplicationMulticastGroup;

/**
 * 
 * EmbeddedModeLifeCycleListenerContainer
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class EmbeddedModeLifeCycleListenerContainer extends LifeCycleListenerContainer {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ApplicationMulticastGroup applicationMulticastGroup;

	@Override
	public void onChange(JobKey jobKey, JobLifeCycle lifeCycle) {
		applicationMulticastGroup.multicast(applicationName, LifeCycleListenerContainer.class.getName(),
				new JobLifeCycleParameter(jobKey, lifeCycle));
	}

}
