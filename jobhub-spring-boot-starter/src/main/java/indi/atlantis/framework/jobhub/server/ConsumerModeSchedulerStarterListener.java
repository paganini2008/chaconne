package indi.atlantis.framework.jobhub.server;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import indi.atlantis.framework.jobhub.JobBeanInitializer;
import indi.atlantis.framework.jobhub.SchedulerStarterListener;
import indi.atlantis.framework.seafloor.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.seafloor.utils.BeanLifeCycle;
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

	@Value("${jobsoup.scheduler.starter.refresh.inititalDelay:5}")
	private int inititalDelay;

	@Value("${jobsoup.scheduler.starter.refresh.checkInterval:60}")
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
