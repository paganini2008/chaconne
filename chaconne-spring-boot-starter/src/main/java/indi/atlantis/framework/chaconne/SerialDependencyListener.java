package indi.atlantis.framework.chaconne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.springdessert.reditools.messager.RedisMessageHandler;
import com.github.paganini2008.springdessert.reditools.messager.RedisMessageSender;

import indi.atlantis.framework.tridenter.LeaderState;
import indi.atlantis.framework.tridenter.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SerialDependencyListener
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class SerialDependencyListener implements ApplicationListener<ApplicationClusterLeaderEvent> {

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		if (event.getLeaderState() == LeaderState.UP) {
			RedisMessageHandler redisMessageHandler = ApplicationContextUtils.instantiateClass(SerialDependencyHandler.class);
			redisMessageSender.subscribeChannel(SerialDependencyHandler.BEAN_NAME, redisMessageHandler);
			log.info("SerialDependencyHandler initialize successfully.");
		}
	}

}
