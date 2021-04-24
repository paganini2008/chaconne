package indi.atlantis.framework.chaconne;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobStarterListener
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@Slf4j
public class JobStarterListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private JobManager jobManager;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		Map<String, Job> beans = applicationContext.getBeansOfType(Job.class);
		beans.values().forEach(job -> {
			try {
				jobManager.persistJob(job, null);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		});
		log.info("Persist {} job-beans after spring context refreshing done", beans.size());
	}

}
