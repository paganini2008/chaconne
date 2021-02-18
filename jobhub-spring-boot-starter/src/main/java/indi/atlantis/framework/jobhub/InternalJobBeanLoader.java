package indi.atlantis.framework.jobhub;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.proxy.Aspect;
import com.github.paganini2008.devtools.proxy.JdkProxyFactory;
import com.github.paganini2008.devtools.proxy.ProxyFactory;
import com.github.paganini2008.devtools.reflection.MethodUtils;

import indi.atlantis.framework.jobhub.model.JobDetail;
import indi.atlantis.framework.jobhub.model.TriggerDescription;
import indi.atlantis.framework.jobhub.model.TriggerDescription.Dependency;
import indi.atlantis.framework.seafloor.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * InternalJobBeanLoader
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class InternalJobBeanLoader implements JobBeanLoader {

	private static final ProxyFactory proxyFactory = new JdkProxyFactory();

	@Autowired
	private JobManager jobManager;

	@Override
	public Job loadJobBean(JobKey jobKey) throws Exception {
		final String jobClassName = jobKey.getJobClassName();
		Class<?> jobClass;
		try {
			jobClass = ClassUtils.forName(jobClassName);
		} catch (RuntimeException e) {
			if (log.isTraceEnabled()) {
				log.trace("Can not load JobClass of name '" + jobClassName + "' to create job instance.");
			}
			return null;
		}
		final String jobName = jobKey.getJobName();
		if (Job.class.isAssignableFrom(jobClass)) {
			Job job = (Job) ApplicationContextUtils.getBean(jobName, jobClass);
			if (job == null) {
				job = (Job) ApplicationContextUtils.getBean(jobClass, bean -> {
					return ((Job) bean).getJobName().equals(jobName);
				});
			}
			if (job == null) {
				throw new JobBeanNotFoundException(jobKey);
			}
			JobDetail jobDetail = jobManager.getJobDetail(jobKey, true);
			if (jobDetail.getJobTriggerDetail().getTriggerType() != TriggerType.DEPENDENT) {
				return job;
			}
			return parallelizeJobIfNecessary(jobKey, job, jobDetail);

		} else if (NotManagedJob.class.isAssignableFrom(jobClass)) {
			NotManagedJob target = (NotManagedJob) ApplicationContextUtils.getBeanIfNecessary(jobClass);
			JobDetail jobDetail = jobManager.getJobDetail(jobKey, true);
			Job job = (Job) proxyFactory.getProxy(target, new JobBeanAspect(jobDetail), Job.class);
			if (jobDetail.getJobTriggerDetail().getTriggerType() != TriggerType.DEPENDENT) {
				return job;
			}
			return parallelizeJobIfNecessary(jobKey, job, jobDetail);
		}
		throw new JobException("Class '" + jobClass.getName() + "' is not a instance of interface '" + Job.class.getName() + "' or '"
				+ NotManagedJob.class.getName() + "'.");
	}

	private Job parallelizeJobIfNecessary(JobKey jobKey, Job job, JobDetail jobDetail) throws Exception {
		Dependency dependency = jobDetail.getJobTriggerDetail().getTriggerDescriptionObject().getDependency();
		if (dependency == null) {
			throw new IllegalJobStateException(jobKey, "Lack of job dependency");
		}
		DependencyType dependencyType = dependency.getDependencyType();
		if (dependencyType == DependencyType.PARALLEL || dependencyType == DependencyType.MIXED) {
			return (Job) proxyFactory.getProxy(job, ApplicationContextUtils.instantiateClass(JobParallelizationAspect.class,
					dependency.getSubKeys(), dependency.getCompletionRate()), Job.class);
		}
		return job;
	}

	/**
	 * 
	 * JobBeanAspect
	 * 
	 * @author Jimmy Hoff
	 *
	 * @since 1.0
	 */
	private static class JobBeanAspect implements Aspect {

		private final JobDetail jobDetail;
		private final TriggerDescription triggerDescription;

		JobBeanAspect(JobDetail jobDetail) {
			this.jobDetail = jobDetail;
			this.triggerDescription = jobDetail.getJobTriggerDetail().getTriggerDescriptionObject();
		}

		@Override
		public Object call(Object target, Method method, Object[] args) {
			final String methodName = method.getName();
			switch (methodName) {
			case "getJobName":
				return jobDetail.getJobKey().getJobName();
			case "getJobClassName":
				return jobDetail.getJobKey().getJobClassName();
			case "getGroupName":
				return jobDetail.getJobKey().getGroupName();
			case "getClusterName":
				return jobDetail.getJobKey().getClusterName();
			case "getDescription":
				return jobDetail.getDescription();
			case "getEmail":
				return jobDetail.getEmail();
			case "getRetries":
				return jobDetail.getRetries();
			case "getTimeout":
				return jobDetail.getTimeout();
			case "getWeight":
				return jobDetail.getWeight();
			case "getTrigger":
				return null;
			case "getDependencyType":
				return triggerDescription.getDependency().getDependencyType();
			case "getDependentKeys":
				return triggerDescription.getDependency().getDependentKeys();
			case "getSubKeys":
				return triggerDescription.getDependency().getSubKeys();
			case "getCompletionRate":
				return triggerDescription.getDependency().getCompletionRate();
			default:
				return MethodUtils.invokeMethod(target, method, args);
			}
		}

	}
}
