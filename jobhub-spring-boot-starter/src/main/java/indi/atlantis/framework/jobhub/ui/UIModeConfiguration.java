package indi.atlantis.framework.jobhub.ui;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import indi.atlantis.framework.jobhub.JobAdmin;
import indi.atlantis.framework.jobhub.JobManager;
import indi.atlantis.framework.jobhub.server.ClusterRestTemplate;
import indi.atlantis.framework.jobhub.server.RestJobManager;
import indi.atlantis.framework.jobhub.server.ServerModeJobAdmin;

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
