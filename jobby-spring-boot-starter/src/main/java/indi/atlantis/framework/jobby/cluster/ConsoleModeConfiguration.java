package indi.atlantis.framework.jobby.cluster;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

import indi.atlantis.framework.jobby.JobAdmin;
import indi.atlantis.framework.jobby.JobManager;

/**
 * 
 * ConsoleModeConfiguration
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@ConditionalOnWebApplication
@Configuration
public class ConsoleModeConfiguration {

	@Bean
	public ClusterRestTemplate clusterRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		return new ConsoleModeClusterRestTemplate(httpRequestFactory);
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
