package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * 
 * ProducerModeRestTemplate
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class ProducerModeRestTemplate extends ClusterRestTemplate {

	public ProducerModeRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		super(httpRequestFactory);
	}

	@Autowired
	private JobServerRegistry clusterRegistry;

	@Override
	protected String[] getClusterContextPaths(String clusterName) {
		return clusterRegistry.getClusterContextPaths(clusterName);
	}

}
