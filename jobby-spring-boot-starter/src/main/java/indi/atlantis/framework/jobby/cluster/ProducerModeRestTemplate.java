package indi.atlantis.framework.jobby.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * 
 * ProducerModeRestTemplate
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ProducerModeRestTemplate extends ClusterRestTemplate {

	public ProducerModeRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		super(httpRequestFactory);
	}

	@Autowired
	private ClusterRegistry clusterRegistry;

	@Override
	protected String[] getClusterContextPaths(String clusterName) {
		return clusterRegistry.getClusterContextPaths(clusterName);
	}

}
