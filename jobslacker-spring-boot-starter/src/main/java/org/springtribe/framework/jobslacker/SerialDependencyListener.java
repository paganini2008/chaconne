package org.springtribe.framework.jobslacker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springtribe.framework.cluster.election.ApplicationClusterLeaderEvent;
import org.springtribe.framework.cluster.utils.ApplicationContextUtils;
import org.springtribe.framework.reditools.messager.RedisMessageHandler;
import org.springtribe.framework.reditools.messager.RedisMessageSender;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SerialDependencyListener
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class SerialDependencyListener implements ApplicationListener<ApplicationClusterLeaderEvent> {

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		RedisMessageHandler redisMessageHandler = ApplicationContextUtils.instantiateClass(SerialDependencyTrigger.class);
		redisMessageSender.subscribeChannel(SerialDependencyTrigger.BEAN_NAME, redisMessageHandler);
		log.info("SerialDependencyTrigger initialize successfully.");
	}

}
