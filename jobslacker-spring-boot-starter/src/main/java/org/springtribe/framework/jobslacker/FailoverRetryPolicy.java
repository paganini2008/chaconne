package org.springtribe.framework.jobslacker;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springtribe.framework.cluster.Constants;
import org.springtribe.framework.cluster.multicast.ApplicationMulticastGroup;
import org.springtribe.framework.jobslacker.model.JobParam;

/**
 * 
 * FailoverRetryPolicy
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class FailoverRetryPolicy implements RetryPolicy {

	@Autowired
	private ApplicationMulticastGroup multicastGroup;

	@Autowired
	private JobManager jobManager;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Override
	public Object retryIfNecessary(JobKey jobKey, Job job, Object attachment, Throwable reason, int retries, Logger log) throws Throwable {
		if (multicastGroup.countOfCandidate(jobKey.getGroupName()) > 0) {
			final String topic = Constants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":scheduler:loadbalance";
			multicastGroup.unicast(jobKey.getGroupName(), topic, new JobParam(jobKey, attachment, retries));
		} else {
			try {
				jobManager.setJobState(jobKey, JobState.SCHEDULING);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		throw reason;
	}

}
