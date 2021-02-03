package org.springtribe.framework.jobslacker.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.jobslacker.JobAdmin;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobLifeCycle;
import org.springtribe.framework.jobslacker.LifeCycleListenerContainer;

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
