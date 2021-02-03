package org.springtribe.framework.jobslacker.ui;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springtribe.framework.jobslacker.JobAdmin;
import org.springtribe.framework.jobslacker.JobManager;
import org.springtribe.framework.jobslacker.server.ClusterRestTemplate;
import org.springtribe.framework.jobslacker.server.RestJobManager;
import org.springtribe.framework.jobslacker.server.ServerModeJobAdmin;

/**
 * 
 * UIModeConfiguration
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@ConditionalOnWebApplication
@Configuration
public class UIModeConfiguration {

	@Bean
	public ClusterRestTemplate clusterRestTemplate() {
		return new UIModeClusterRestTemplate();
	}

	@Bean
	@ConditionalOnMissingBean(JobManager.class)
	public JobManager jobManager() {
		return new RestJobManager();
	}

	@Bean
	public JobAdmin jobAdmin() {
		return new ServerModeJobAdmin();
	}
}
