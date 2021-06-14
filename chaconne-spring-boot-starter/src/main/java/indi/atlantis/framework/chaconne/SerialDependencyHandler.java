package indi.atlantis.framework.chaconne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springdessert.reditools.messager.RedisMessageHandler;

import indi.atlantis.framework.chaconne.model.JobParameter;
import indi.atlantis.framework.tridenter.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SerialDependencyHandler
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class SerialDependencyHandler implements RedisMessageHandler {

	static final String BEAN_NAME = SerialDependencyHandler.class.getSimpleName();

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
		final JobParameter jobParam = (JobParameter) message;
		if (log.isTraceEnabled()) {
			log.trace("Trigger all serial dependencies by Job: " + jobParam.getJobKey());
		}
		serialDependencyScheduler.triggerDependency(jobParam.getJobKey(), jobParam.getAttachment());
	}

}
