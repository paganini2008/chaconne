package indi.atlantis.framework.chaconne;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.springdessert.reditools.RedisComponentNames;
import com.github.paganini2008.springdessert.reditools.common.RedisCountDownLatch;

import indi.atlantis.framework.chaconne.model.TaskForkResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ForkJoinJobListener
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Slf4j
public class ForkJoinJobListener implements JobListener {

	@Autowired
	@Qualifier(RedisComponentNames.REDIS_TEMPLATE)
	private RedisTemplate<String, Object> redisTemplate;

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
		RedisCountDownLatch latch = new RedisCountDownLatch(relation.getIdentifier(), redisTemplate);
		latch.countDown(new TaskForkResult(jobKey, attachment, runningState, result));
		if (log.isTraceEnabled()) {
			log.trace("Release job latch: {}", latch);
		}
	}

}
