package org.springtribe.framework.jobslacker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springtribe.framework.cluster.Constants;
import org.springtribe.framework.jobslacker.model.JobParam;
import org.springtribe.framework.reditools.messager.RedisMessageHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SerialDependencyTrigger
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class SerialDependencyTrigger implements RedisMessageHandler {

	static final String BEAN_NAME = "serialDependencyTrigger";

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private SerialDependencyScheduler serialDependencyScheduler;

	@Override
	public String getChannel() {
		return Constants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":scheduler:job:dependency:*";
	}

	@Override
	public void onMessage(String channel, Object message) throws Exception {
		final JobParam jobParam = (JobParam) message;
		if (log.isTraceEnabled()) {
			log.trace("Trigger all serial dependencies by Job: " + jobParam.getJobKey());
		}
		serialDependencyScheduler.triggerDependency(jobParam.getJobKey(), jobParam.getAttachment());
	}

}
