package indi.atlantis.framework.chaconne.cluster;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

import indi.atlantis.framework.chaconne.JobAdmin;
import indi.atlantis.framework.chaconne.JobManager;

/**
 * 
 * ClientModeConfiguration
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
public class ClientModeConfiguration {

	@Bean
	public ClusterRestTemplate clusterRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		return new ClientModeClusterRestTemplate(httpRequestFactory);
	}

	@Bean
	@ConditionalOnMissingBean(JobManager.class)
	public JobManager jobManager() {
		return new RestJobManager();
	}

	@Bean
	public JobAdmin jobAdmin() {
		return new DetachedModeJobAdmin();
	}
}
