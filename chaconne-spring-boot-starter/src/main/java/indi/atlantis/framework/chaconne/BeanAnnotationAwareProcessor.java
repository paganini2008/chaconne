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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.DateUtils;

import indi.atlantis.framework.chaconne.annotations.ChacDependency;
import indi.atlantis.framework.chaconne.annotations.ChacFork;
import indi.atlantis.framework.chaconne.annotations.ChacJob;
import indi.atlantis.framework.chaconne.annotations.ChacJobKey;
import indi.atlantis.framework.chaconne.annotations.ChacTrigger;
import indi.atlantis.framework.chaconne.utils.GenericJobDefinition;
import indi.atlantis.framework.chaconne.utils.GenericTrigger;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * BeanAnnotationAwareProcessor
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Slf4j
public class BeanAnnotationAwareProcessor implements BeanPostProcessor {

	private static final String[] datePatterns = new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" };

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private JobManager jobManager;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean.getClass().isAnnotationPresent(ChacJob.class)) {
			JobDefinition jobDefinition = parseObject(bean, beanName);
			try {
				jobManager.persistJob(jobDefinition, null);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return bean;
	}

	private JobDefinition parseObject(Object bean, String beanName) {
		final Class<?> jobBeanClass = bean.getClass();
		final ChacJob job = jobBeanClass.getAnnotation(ChacJob.class);
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

}
