package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.chaconne.JobAdmin;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobLifeCycle;
import indi.atlantis.framework.chaconne.LifeCycleListenerContainer;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DetachedModeLifeCycleListenerContainer
 * 
 * @author Fred Feng
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
