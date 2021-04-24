package indi.atlantis.framework.chaconne;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.chaconne.model.JobParam;
import indi.atlantis.framework.tridenter.Constants;
import indi.atlantis.framework.tridenter.multicast.ApplicationMulticastGroup;

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
	private ApplicationMulticastGroup applicationMulticastGroup;

	@Autowired
	private JobManager jobManager;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Override
	public Object retryIfNecessary(JobKey jobKey, Job job, Object attachment, Throwable reason, int retries, Logger log) throws Throwable {
		if (applicationMulticastGroup.countOfCandidate(jobKey.getGroupName()) > 0) {
			final String topic = Constants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":scheduler:loadbalance";
			applicationMulticastGroup.unicast(jobKey.getGroupName(), topic, new JobParam(jobKey, attachment, retries));
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
