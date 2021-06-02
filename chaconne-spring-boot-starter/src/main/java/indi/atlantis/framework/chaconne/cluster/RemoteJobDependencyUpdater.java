package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.chaconne.JobDependencyUpdater;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.TriggerType;
import indi.atlantis.framework.chaconne.model.JobKeyQuery;

/**
 * 
 * RemoteJobDependencyUpdater
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class RemoteJobDependencyUpdater extends JobDependencyUpdater {

	@Value("${atlantis.framework.chaconne.producer.job.clusterNames:}")
	private String clusterNames;

	@Value("${atlantis.framework.chaconne.producer.job.groupNames:}")
	private String groupNames;

	@Autowired
	private JobManager jobManager;

	@Override
	protected JobKey[] selectDependentKeys() throws Exception {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		if (StringUtils.isNotBlank(clusterNames)) {
			jobQuery.setClusterNames(clusterNames);
		} else if (StringUtils.isNotBlank(groupNames)) {
			jobQuery.setGroupNames(groupNames);
		}
		return jobManager.getJobKeys(jobQuery);
	}

}
