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

import java.lang.reflect.Method;

import com.github.paganini2008.devtools.proxy.Aspect;
import com.github.paganini2008.devtools.proxy.JdkProxyFactory;
import com.github.paganini2008.devtools.proxy.ProxyFactory;
import com.github.paganini2008.devtools.reflection.MethodUtils;

import io.atlantisframework.chaconne.model.JobDetail;
import io.atlantisframework.chaconne.model.TriggerDescription;

/**
 * 
 * JobBeanProxyUtils
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
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
			case "getForkKeys":
				return triggerDescription.getDependency().getForkKeys();
			case "getCompletionRate":
				return triggerDescription.getDependency().getCompletionRate();
			default:
				return MethodUtils.invokeMethod(target, method, args);
			}
		}

	}

}
