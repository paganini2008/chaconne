package indi.atlantis.framework.chaconne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.springworld.reditools.messager.RedisMessageHandler;
import com.github.paganini2008.springworld.reditools.messager.RedisMessageSender;

import indi.atlantis.framework.tridenter.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;
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
