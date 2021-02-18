package indi.atlantis.framework.jobhub.server;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * ProducerModeRestTemplate
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ProducerModeRestTemplate extends ClusterRestTemplate {

	@Autowired
	private ClusterRegistry clusterRegistry;

	@Override
	protected String[] getClusterContextPaths(String clusterName) {
		return clusterRegistry.getClusterContextPaths(clusterName);
	}

}
