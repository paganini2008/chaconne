package indi.atlantis.framework.jobby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.jobby.model.JobParam;
import indi.atlantis.framework.reditools.messager.RedisMessageHandler;
import indi.atlantis.framework.seafloor.Constants;
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
