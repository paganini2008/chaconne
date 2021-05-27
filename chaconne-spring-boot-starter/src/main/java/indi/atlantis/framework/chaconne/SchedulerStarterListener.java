package indi.atlantis.framework.chaconne;

import indi.atlantis.framework.tridenter.election.ApplicationClusterLeaderEvent;

/**
 * 
 * SchedulerStarterListener
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface SchedulerStarterListener {

	void onApplicationEvent(ApplicationClusterLeaderEvent event);
}
