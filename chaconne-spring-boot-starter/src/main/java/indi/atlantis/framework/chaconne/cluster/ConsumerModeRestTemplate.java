package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * 
 * ConsumerModeRestTemplate
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ConsumerModeRestTemplate extends ClusterRestTemplate {

	public ConsumerModeRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		super(httpRequestFactory);
	}

	@Value("${atlantis.framework.chaconne.producer.location}")
	private String contextPaths;

	@Override
	protected String[] getClusterContextPaths(String clusterName) {
		return this.contextPaths.split(",");
	}

}
