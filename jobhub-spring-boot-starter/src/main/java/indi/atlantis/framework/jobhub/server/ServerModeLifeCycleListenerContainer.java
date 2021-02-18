package indi.atlantis.framework.jobhub.server;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.jobhub.JobAdmin;
import indi.atlantis.framework.jobhub.JobKey;
import indi.atlantis.framework.jobhub.JobLifeCycle;
import indi.atlantis.framework.jobhub.LifeCycleListenerContainer;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ServerModeLifeCycleListenerContainer
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class ServerModeLifeCycleListenerContainer extends LifeCycleListenerContainer {

	@Autowired
	private JobAdmin jobAdmin;

	@Override
	public void onChange(JobKey jobKey, JobLifeCycle lifeCycle) {
		try {
			jobAdmin.publicLifeCycleEvent(jobKey, lifeCycle);
		} catch (NoJobResourceException e) {
			log.warn("Job: " + jobKey.toString() + " is not available now.");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
