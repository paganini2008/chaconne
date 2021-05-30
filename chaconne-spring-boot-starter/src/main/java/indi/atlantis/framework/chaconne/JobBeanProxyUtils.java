package indi.atlantis.framework.chaconne;

import java.lang.reflect.Method;

import com.github.paganini2008.devtools.proxy.Aspect;
import com.github.paganini2008.devtools.proxy.JdkProxyFactory;
import com.github.paganini2008.devtools.proxy.ProxyFactory;
import com.github.paganini2008.devtools.reflection.MethodUtils;

import indi.atlantis.framework.chaconne.model.JobDetail;
import indi.atlantis.framework.chaconne.model.TriggerDescription;

/**
 * 
 * JobBeanProxyUtils
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public abstract class JobBeanProxyUtils {

	private static ProxyFactory proxyFactory = new JdkProxyFactory();

	static void setProxyFactory(ProxyFactory proxyFactory) {
		JobBeanProxyUtils.proxyFactory = proxyFactory;
	}

	public static Job getBeanProxy(Object delegate, JobDetail jobDetail) {
		return getBeanProxy(delegate, new JobBeanAspect(jobDetail));
	}

	public static Job getBeanProxy(Object delegate, Aspect aspect) {
		return (Job) proxyFactory.getProxy(delegate, aspect, Job.class);
	}

	private static class JobBeanAspect implements Aspect {

		private final JobDetail jobDetail;
		private final TriggerDescription triggerDescription;

		private JobBeanAspect(JobDetail jobDetail) {
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
			case "getSubJobKeys":
				return triggerDescription.getDependency().getSubJobKeys();
			case "getCompletionRate":
				return triggerDescription.getDependency().getCompletionRate();
			default:
				return MethodUtils.invokeMethod(target, method, args);
			}
		}

	}

}
