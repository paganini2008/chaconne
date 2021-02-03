package org.springtribe.framework.jobslacker;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springtribe.framework.jobslacker.model.JobPeerResult;
import org.springtribe.framework.reditools.common.RedisCountDownLatch;
import org.springtribe.framework.reditools.messager.RedisMessageSender;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobParallelizationListener
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class JobParallelizationListener implements JobRuntimeListener {

	@Autowired
	private RedisMessageSender redisMessageSender;

	@Autowired
	private JobManager jobManager;

	@Override
	public void afterRun(long traceId, JobKey jobKey, Object attachment, Date startDate, RunningState runningState, Object result,
			Throwable reason) {
		JobKey relation;
		try {
			relation = jobManager.getRelations(jobKey, DependencyType.PARALLEL)[0];
		} catch (Exception e) {
			throw ExceptionUtils.wrapExeception("Job '" + jobKey + "' has no relations", e);
		}
		RedisCountDownLatch latch = new RedisCountDownLatch(relation.getIdentifier(), redisMessageSender);
		latch.countdown(new JobPeerResult(jobKey, attachment, runningState, result));
		if (log.isTraceEnabled()) {
			log.trace("Release job latch: {}", latch);
		}
	}

}
