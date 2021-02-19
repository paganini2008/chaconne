package indi.atlantis.framework.jobby.cluster;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.jobby.JobAdmin;
import indi.atlantis.framework.jobby.JobKey;
import indi.atlantis.framework.jobby.JobLifeCycle;
import indi.atlantis.framework.jobby.LifeCycleListenerContainer;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DetachedModeLifeCycleListenerContainer
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class DetachedModeLifeCycleListenerContainer extends LifeCycleListenerContainer {

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
