package indi.atlantis.framework.jobby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import indi.atlantis.framework.reditools.messager.RedisMessageHandler;
import indi.atlantis.framework.reditools.messager.RedisMessageSender;
import indi.atlantis.framework.seafloor.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.seafloor.utils.ApplicationContextUtils;
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
