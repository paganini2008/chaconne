package indi.atlantis.framework.chaconne.cluster;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import indi.atlantis.framework.chaconne.JobBeanInitializer;
import indi.atlantis.framework.chaconne.SchedulerStarterListener;
import indi.atlantis.framework.tridenter.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ConsumerModeSchedulerStarterListener
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class ConsumerModeSchedulerStarterListener
		implements ApplicationListener<ApplicationClusterLeaderEvent>, Executable, SchedulerStarterListener, BeanLifeCycle {

	@Autowired(required = false)
	private JobBeanInitializer jobBeanInitializer;

	@Value("${atlantis.framework.chaconne.scheduler.starter.inititalDelay:5}")
	private int inititalDelay;

	@Value("${atlantis.framework.chaconne.scheduler.starter.checkInterval:60}")
	private int checkInterval;

	private Timer timer;

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		timer = ThreadUtils.scheduleWithFixedDelay(this, inititalDelay, checkInterval, TimeUnit.SECONDS);
	}

	@Override
	public boolean execute() {
		if (jobBeanInitializer != null) {
			try {
				jobBeanInitializer.initializeJobBeans();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return jobBeanInitializer != null;
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
		}
	}

}
