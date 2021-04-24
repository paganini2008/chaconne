package indi.atlantis.framework.chaconne;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import indi.atlantis.framework.chaconne.model.JobKeyQuery;
import indi.atlantis.framework.chaconne.model.JobTriggerDetail;
import indi.atlantis.framework.chaconne.model.TriggerDescription.Dependency;
import indi.atlantis.framework.tridenter.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobDependencyFutureListener
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class JobDependencyFutureListener implements ApplicationListener<ApplicationClusterLeaderEvent>, Executable, BeanLifeCycle {

	private Timer timer;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobDao jobDao;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Override
	public boolean execute() {
		refresh();
		return true;
	}

	private void refresh() {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setClusterName(clusterName);
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		JobKey[] jobKeys = new JobKey[0];
		try {
			jobKeys = jobManager.getJobKeys(jobQuery);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		Map<JobKey, JobKey[]> serialDependencies = new HashMap<JobKey, JobKey[]>();
		Map<JobKey, JobKey[]> parallelDependencies = new HashMap<JobKey, JobKey[]>();
		JobKey[] dependentKeys;
		JobKey[] comparedDependentKeys;
		JobKey[] requiredJobKeys;
		JobTriggerDetail triggerDetail;
		Dependency dependency;
		for (JobKey jobKey : jobKeys) {
			try {
				triggerDetail = jobManager.getJobTriggerDetail(jobKey);
				dependency = triggerDetail.getTriggerDescriptionObject().getDependency();

				switch (dependency.getDependencyType()) {
				case SERIAL:
					dependentKeys = dependency.getDependentKeys();
					comparedDependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.SERIAL);
					requiredJobKeys = ArrayUtils.minus(dependentKeys, comparedDependentKeys);
					if (ArrayUtils.isNotEmpty(requiredJobKeys)) {
						serialDependencies.put(jobKey, requiredJobKeys);
					}
					break;
				case PARALLEL:
					dependentKeys = dependency.getSubKeys();
					comparedDependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.PARALLEL);
					requiredJobKeys = ArrayUtils.minus(dependentKeys, comparedDependentKeys);
					if (ArrayUtils.isNotEmpty(requiredJobKeys)) {
						parallelDependencies.put(jobKey, requiredJobKeys);
					}
					break;
				case MIXED:
					dependentKeys = dependency.getDependentKeys();
					comparedDependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.SERIAL);
					requiredJobKeys = ArrayUtils.minus(dependentKeys, comparedDependentKeys);
					if (ArrayUtils.isNotEmpty(requiredJobKeys)) {
						serialDependencies.put(jobKey, requiredJobKeys);
					}

					dependentKeys = dependency.getSubKeys();
					comparedDependentKeys = jobManager.getDependentKeys(jobKey, DependencyType.PARALLEL);
					requiredJobKeys = ArrayUtils.minus(dependentKeys, comparedDependentKeys);
					if (ArrayUtils.isNotEmpty(requiredJobKeys)) {
						parallelDependencies.put(jobKey, requiredJobKeys);
					}
					break;
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		for (Map.Entry<JobKey, JobKey[]> entry : serialDependencies.entrySet()) {
			JobKey jobKey = entry.getKey();
			JobKey[] keys = entry.getValue();
			if (ArrayUtils.isNotEmpty(keys)) {
				for (JobKey key : keys) {
					try {
						if (jobManager.hasJob(key)) {
							saveJobDependency(jobKey, key, DependencyType.SERIAL);
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}

		for (Map.Entry<JobKey, JobKey[]> entry : parallelDependencies.entrySet()) {
			JobKey jobKey = entry.getKey();
			JobKey[] keys = entry.getValue();
			if (ArrayUtils.isNotEmpty(keys)) {
				for (JobKey key : keys) {
					try {
						if (jobManager.hasJob(key)) {
							saveJobDependency(jobKey, key, DependencyType.PARALLEL);
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}

	}

	private void saveJobDependency(JobKey jobKey, JobKey dependency, DependencyType dependencyType) throws Exception {
		final int jobId = jobManager.getJobId(jobKey);
		final int dependentId = jobManager.getJobId(dependency);
		Map<String, Object> kwargs = new HashMap<String, Object>();
		kwargs.put("jobId", jobId);
		kwargs.put("dependentId", dependentId);
		kwargs.put("dependencyType", dependencyType.getValue());
		jobDao.saveJobDependency(kwargs);
		log.info("Add job dependency '{}' to jobId {} ok.", dependency, jobId);
	}

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		this.timer = ThreadUtils.scheduleWithFixedDelay(this, 1, TimeUnit.MINUTES);
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}