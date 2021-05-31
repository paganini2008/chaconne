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
import com.github.paganini2008.devtools.ObjectUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.date.DateUtils;

import indi.atlantis.framework.chaconne.annotations.Dependency;
import indi.atlantis.framework.chaconne.annotations.Job;
import indi.atlantis.framework.chaconne.annotations.JobKey;
import indi.atlantis.framework.chaconne.annotations.Trigger;
import indi.atlantis.framework.chaconne.utils.GenericJobDefinition;
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
		if (bean.getClass().isAnnotationPresent(Job.class)) {
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
		final Job job = jobBeanClass.getAnnotation(Job.class);
		Trigger trigger = jobBeanClass.getAnnotation(Trigger.class);
		String jobName = StringUtils.isNotBlank(job.name()) ? job.name() : beanName;
		GenericJobDefinition.Builder builder = GenericJobDefinition.newJob(clusterName, applicationName, jobName, jobBeanClass);
		builder.setDescription(job.description()).setTimeout(job.timeout()).setEmail(job.email()).setRetries(job.retries())
				.setWeight(job.weight());
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
			if (triggerType == TriggerType.DEPENDENT) {
				Dependency dependency = jobBeanClass.getAnnotation(Dependency.class);
				builder.setCompletionRate(dependency.completionRate());
				JobKey[] jobKeys = dependency.dependentKeys();
				if (ArrayUtils.isNotEmpty(jobKeys)) {
					List<indi.atlantis.framework.chaconne.JobKey> dependentKeys = new ArrayList<>();
					for (JobKey jobKey : jobKeys) {
						dependentKeys.add(indi.atlantis.framework.chaconne.JobKey.by(ObjectUtils.toString(jobKey.cluster(), clusterName),
								ObjectUtils.toString(jobKey.group(), applicationName), jobKey.name(), jobKey.className()));
					}
					builder.setDependentKeys(dependentKeys.toArray(new indi.atlantis.framework.chaconne.JobKey[0]));
				}
				jobKeys = dependency.subJobKeys();
				if (ArrayUtils.isNotEmpty(jobKeys)) {
					List<indi.atlantis.framework.chaconne.JobKey> subJobKeys = new ArrayList<>();
					for (JobKey jobKey : jobKeys) {
						subJobKeys.add(indi.atlantis.framework.chaconne.JobKey.by(ObjectUtils.toString(jobKey.cluster(), clusterName),
								ObjectUtils.toString(jobKey.group(), applicationName), jobKey.name(), jobKey.className()));
					}
					builder.setSubJobKeys(subJobKeys.toArray(new indi.atlantis.framework.chaconne.JobKey[0]));
				}
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
		return builder.build();
	}

}
