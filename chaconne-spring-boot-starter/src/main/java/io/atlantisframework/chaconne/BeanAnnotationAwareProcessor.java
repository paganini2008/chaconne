/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package io.atlantisframework.chaconne;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.multithreads.RetryableTimer;
import com.github.paganini2008.devtools.time.DateUtils;

import io.atlantisframework.chaconne.annotations.ChacDependency;
import io.atlantisframework.chaconne.annotations.ChacFork;
import io.atlantisframework.chaconne.annotations.ChacJob;
import io.atlantisframework.chaconne.annotations.ChacJobKey;
import io.atlantisframework.chaconne.annotations.ChacTrigger;
import io.atlantisframework.chaconne.cluster.RestClientRetryable;
import io.atlantisframework.chaconne.cluster.RestJobManager;
import io.atlantisframework.chaconne.utils.GenericJobDefinition;
import io.atlantisframework.chaconne.utils.GenericTrigger;

/**
 * 
 * BeanAnnotationAwareProcessor
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class BeanAnnotationAwareProcessor implements BeanPostProcessor {

	private static final String[] datePatterns = new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" };

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private JobManager jobManager;

	@Autowired(required = false)
	private RetryableTimer retryableTimer;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean.getClass().isAnnotationPresent(ChacJob.class)) {
			final ChacJob job = bean.getClass().getAnnotation(ChacJob.class);
			JobDefinition jobDefinition = parseObject(bean, beanName, job);
			if (jobManager instanceof RestJobManager && retryableTimer != null) {
				retryableTimer.executeAndRetryWithFixedDelay(new PersistRetrier(jobDefinition, job.attachment()),
						RestClientRetryable.DEFAULT_RETRY_INTERVAL, TimeUnit.SECONDS);
			} else {
				try {
					jobManager.persistJob(jobDefinition, job.attachment());
				} catch (Exception e) {
					throw new BeanInitializationException(e.getMessage(), e);
				}
			}
		}
		return bean;
	}

	private JobDefinition parseObject(Object bean, String beanName, ChacJob job) {
		final Class<?> jobBeanClass = bean.getClass();
		String jobName = StringUtils.isNotBlank(job.name()) ? job.name() : beanName;
		GenericJobDefinition.Builder builder = GenericJobDefinition.newJob(clusterName, applicationName, jobName, jobBeanClass);
		builder.setDescription(job.description()).setTimeout(job.timeout()).setEmail(job.email()).setRetries(job.retries())
				.setWeight(job.weight());
		ChacTrigger trigger = jobBeanClass.getAnnotation(ChacTrigger.class);
		if (trigger != null) {
			GenericTrigger.Builder triggerBuilder = GenericTrigger.Builder.newTrigger();
			TriggerType triggerType = trigger.triggerType();
			switch (triggerType) {
			case CRON:
				triggerBuilder = GenericTrigger.Builder.newTrigger(trigger.cron());
				break;
			case PERIODIC:
				triggerBuilder = GenericTrigger.Builder.newTrigger(trigger.period(), trigger.schedulingUnit(), trigger.fixedRate());
				break;
			default:
				triggerBuilder = GenericTrigger.Builder.newTrigger().setTriggerType(triggerType);
				break;
			}

			if (trigger.delay() > 0) {
				long amount = trigger.schedulingUnit().getTimeUnit().convert(trigger.delay(), TimeUnit.SECONDS);
				triggerBuilder.setStartDate(DateUtils.addSeconds(new Date(), (int) amount));
			}
			if (StringUtils.isNotBlank(trigger.startDate())) {
				triggerBuilder.setStartDate(DateUtils.parse(trigger.startDate(), datePatterns));
			}
			if (StringUtils.isNotBlank(trigger.endDate())) {
				triggerBuilder.setEndDate(DateUtils.parse(trigger.endDate(), datePatterns));
			}

			triggerBuilder.setRepeatCount(trigger.repeatCount());
			builder.setTrigger(triggerBuilder.build());
		}
		ChacDependency dependency = jobBeanClass.getAnnotation(ChacDependency.class);
		if (dependency != null) {
			ChacJobKey[] jobKeys = dependency.value();
			if (ArrayUtils.isNotEmpty(jobKeys)) {
				List<JobKey> dependentKeys = new ArrayList<>();
				for (ChacJobKey jobKey : jobKeys) {
					dependentKeys.add(JobKey.by((StringUtils.isNotBlank(jobKey.cluster()) ? jobKey.cluster() : clusterName),
							(StringUtils.isNotBlank(jobKey.group()) ? jobKey.group() : applicationName), jobKey.name(),
							jobKey.className()));
				}
				builder.setDependentKeys(dependentKeys.toArray(new JobKey[0]));
			}
		}
		ChacFork fork = jobBeanClass.getAnnotation(ChacFork.class);
		if (fork != null) {
			builder.setCompletionRate(fork.completionRate());
			ChacJobKey[] jobKeys = fork.value();
			if (ArrayUtils.isNotEmpty(jobKeys)) {
				List<JobKey> forkKeys = new ArrayList<>();
				for (ChacJobKey jobKey : jobKeys) {
					forkKeys.add(JobKey.by((StringUtils.isNotBlank(jobKey.cluster()) ? jobKey.cluster() : clusterName),
							(StringUtils.isNotBlank(jobKey.group()) ? jobKey.group() : applicationName), jobKey.name(),
							jobKey.className()));
				}
				builder.setForkKeys(forkKeys.toArray(new JobKey[0]));
			}
		}

		return builder.build();
	}

	private class PersistRetrier extends RestClientRetryable {

		private final JobDefinition jobDefinition;
		private final String attachment;

		PersistRetrier(JobDefinition jobDefinition, String attachment) {
			this.jobDefinition = jobDefinition;
			this.attachment = attachment;
		}

		@Override
		public void execute() throws Throwable {
			jobManager.persistJob(jobDefinition, attachment);
		}

	}

}
