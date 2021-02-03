package org.springtribe.framework.jobslacker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springtribe.framework.cluster.election.ApplicationClusterRefreshedEvent;
import org.springtribe.framework.cluster.utils.ApplicationContextUtils;
import org.springtribe.framework.jobslacker.model.JobKeyQuery;

import com.github.paganini2008.devtools.ArrayUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * EmbeddedModeStarterListener
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class EmbeddedModeStarterListener implements ApplicationListener<ApplicationClusterRefreshedEvent> {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobRuntimeListenerContainer jobRuntimeListenerContainer;

	@Override
	public void onApplicationEvent(ApplicationClusterRefreshedEvent event) {
		handleParallelDependencies();
	}

	private void handleParallelDependencies() {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setClusterName(clusterName);
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		try {
			JobKey[] jobKeys = jobManager.getJobKeys(jobQuery);
			if (ArrayUtils.isNotEmpty(jobKeys)) {
				JobKey[] dependentKeys;
				for (JobKey jobKey : jobKeys) {
					// add listener to watch parallel dependency job done
					dependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.PARALLEL);
					if (ArrayUtils.isNotEmpty(dependentKeys)) {
						for (JobKey dependency : dependentKeys) {
							jobRuntimeListenerContainer.addListener(dependency,
									ApplicationContextUtils.instantiateClass(JobParallelizationListener.class));
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
