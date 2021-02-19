package indi.atlantis.framework.jobby.cluster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * 
 * ConsoleModeClusterRestTemplate
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ConsoleModeClusterRestTemplate extends ClusterRestTemplate {

	public ConsoleModeClusterRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		super(httpRequestFactory);
	}

	@Value("${atlantis.framework.jobhub.server.mode.producer.location}")
	private String contextPaths;

	@Override
	protected String[] getClusterContextPaths(String clusterName) {
		return this.contextPaths.split(",");
	}

}
