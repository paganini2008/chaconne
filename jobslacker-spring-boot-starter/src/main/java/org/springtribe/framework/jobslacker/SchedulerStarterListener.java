package org.springtribe.framework.jobslacker;

import org.springtribe.framework.cluster.election.ApplicationClusterLeaderEvent;

/**
 * 
 * SchedulerStarterListener
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface SchedulerStarterListener {

	void onApplicationEvent(ApplicationClusterLeaderEvent event);
}
