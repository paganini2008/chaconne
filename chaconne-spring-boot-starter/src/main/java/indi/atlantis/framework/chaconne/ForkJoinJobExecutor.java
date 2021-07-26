/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package indi.atlantis.framework.chaconne;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.NumberUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.Duration;
import com.github.paganini2008.devtools.proxy.Aspect;
import com.github.paganini2008.devtools.reflection.MethodUtils;
import com.github.paganini2008.springdessert.reditools.common.RedisCountDownLatch;

import indi.atlantis.framework.chaconne.model.JobDetail;
import indi.atlantis.framework.chaconne.model.JobResult;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ForkJoinJobExecutor
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class ForkJoinJobExecutor implements Aspect {

	private final JobKey[] dependencies;
	private final float completionRate;

	ForkJoinJobExecutor(JobKey[] dependencies, float completionRate) {
		this.dependencies = dependencies;
		this.completionRate = completionRate;
	}

	@Qualifier(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
	@Autowired
	private JobExecutor jobExecutor;

	@Qualifier(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
	@Autowired
	private JobBeanLoader jobBeanLoader;

	@Qualifier(ChaconneBeanNames.EXTERNAL_JOB_BEAN_LOADER)
	@Autowired(required = false)
	private JobBeanLoader externalJobBeanLoader;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JobManager jobManager;

	@Override
	public Object call(Object target, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("execute")) {
			final Job targetJob = (Job) target;
			final JobKey jobKey = (JobKey) args[0];
			final Object attachment = args[1];
			final Logger logger = (Logger) args[2];
			if (log.isDebugEnabled()) {
				log.debug("Parallel run job with JobKey: {}, Attachment: {}", jobKey, attachment);
			}

			log.trace("First parallel run all dependent jobs ...");
			long startTime = System.currentTimeMillis();
			long maxTimeout = 0;
			for (JobKey dependency : dependencies) {
				log.trace("Parallel run dependent job: " + dependency);
				Job job = getJob(dependency);
				JobDetail jobDetail = jobManager.getJobDetail(dependency, false);
				jobExecutor.execute(job, StringUtils.isNotBlank(jobDetail.getAttachment()) ? jobDetail.getAttachment() : attachment, 0);
				maxTimeout = Long.max(maxTimeout, job.getTimeout());
			}
			RedisCountDownLatch latch = new RedisCountDownLatch(jobKey.getIdentifier(), redisTemplate, dependencies.length);
			Object[] answers = maxTimeout > 0 ? latch.await(maxTimeout, TimeUnit.MILLISECONDS) : latch.await();
			if (ArrayUtils.isNotEmpty(answers)) {
				if (answers.length == dependencies.length) {
					log.trace("All dependent jobs have done. Take time: " + Duration.HOUR.format(System.currentTimeMillis() - startTime));
				} else {
					log.warn("Maybe some dependent job spend too much time. Please check them.");
				}
				JobResult[] forkJobResults = ArrayUtils.cast(answers, JobResult.class);
				int totalWeight = 0, completionWeight = 0;
				for (JobResult jobResult : forkJobResults) {
					JobDetail jobDetail = jobManager.getJobDetail(jobResult.getJobKey(), false);
					totalWeight += jobDetail.getWeight();
					completionWeight += approveIfJobCompleted(targetJob, jobResult) ? jobDetail.getWeight() : 0;
				}
				float rate = totalWeight > 0 ? (float) completionWeight / totalWeight : 0;
				if (rate >= completionRate) {
					log.trace("The completionRate is '{}' and now start to run target job '{}'.", NumberUtils.format(rate, 2), jobKey);
					JobResult jobResult = new JobResult(jobKey, attachment, null, null);
					jobResult.setForkJobResults(forkJobResults);
					return targetJob.execute(jobKey, jobResult, logger);
				}
			}
			return null;
		}
		return MethodUtils.invokeMethod(target, method, args);
	}

	private boolean approveIfJobCompleted(Job targetJob, JobResult jobResult) {
		if (ArrayUtils.isNotEmpty(targetJob.getDependencyPostHandlers())) {
			boolean result = true;
			for (Class<?> handlerClass : targetJob.getDependencyPostHandlers()) {
				DependencyPostHandler handler = (DependencyPostHandler) ApplicationContextUtils.getOrCreateBean(handlerClass);
				result &= handler.approve(jobResult.getJobKey(), jobResult.getRunningState(), jobResult.getAttachment(),
						jobResult.getResult());
			}
			return result;
		} else {
			return jobResult.getRunningState() == RunningState.COMPLETED;
		}
	}

	private Job getJob(JobKey jobKey) throws Exception {
		Job job = jobBeanLoader.loadJobBean(jobKey);
		if (job == null && externalJobBeanLoader != null) {
			job = externalJobBeanLoader.loadJobBean(jobKey);
		}
		return job;
	}

}
