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

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.ClassUtils;
import com.github.paganini2008.devtools.proxy.Aspect;

import io.atlantisframework.chaconne.model.JobDetail;
import io.atlantisframework.chaconne.model.TriggerDescription.Dependency;
import io.atlantisframework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * InternalJobBeanLoader
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class InternalJobBeanLoader implements JobBeanLoader {

	@Autowired
	private JobManager jobManager;

	@Override
	public Job loadJobBean(JobKey jobKey) throws Exception {
		final String jobClassName = jobKey.getJobClassName();
		if (!ClassUtils.isPresent(jobClassName)) {
			if (log.isTraceEnabled()) {
				log.trace("Can not load JobClass '" + jobClassName + "' to create job instance.");
			}
			return null;
		}
		Class<?> jobClass = ClassUtils.forName(jobClassName);
		JobDetail jobDetail = jobManager.getJobDetail(jobKey, true);
		Job job = getOrCreateJobBean(jobClass, jobKey, jobDetail);
		if (jobDetail.getJobTriggerDetail().getTriggerType() != TriggerType.DEPENDENT) {
			return job;
		}
		return getForkJoinBeanProxyIfNecessary(jobKey, job, jobDetail);
	}

	private Job getOrCreateJobBean(Class<?> jobClass, JobKey jobKey, JobDetail jobDetail) {
		if (Job.class.isAssignableFrom(jobClass)) {
			return JobUtils.getJobBean(jobKey.getJobName(), jobClass);
		} else if (NotManagedJob.class.isAssignableFrom(jobClass)) {
			NotManagedJob jobBean = (NotManagedJob) ApplicationContextUtils.getOrCreateBean(jobClass);
			return JobBeanProxyUtils.getBeanProxy(jobBean, jobDetail);
		} else if (jobClass.isAnnotationPresent(io.atlantisframework.chaconne.annotations.ChacJob.class)) {
			Object targetBean = ApplicationContextUtils.getBean(jobClass);
			NotManagedJob jobBean = new AnnotatedJobBeanProxy(targetBean);
			return JobBeanProxyUtils.getBeanProxy(jobBean, jobDetail);
		}
		throw new IllegalJobStateException(jobKey, "Unsupported job class: " + jobClass.getName());
	}

	private Job getForkJoinBeanProxyIfNecessary(JobKey jobKey, Job job, JobDetail jobDetail) throws Exception {
		Dependency dependency = jobDetail.getJobTriggerDetail().getTriggerDescriptionObject().getDependency();
		if (dependency == null) {
			throw new IllegalJobStateException(jobKey, "Lack of job dependency");
		}
		DependencyType dependencyType = dependency.getDependencyType();
		if (dependencyType == DependencyType.PARALLEL || dependencyType == DependencyType.MIXED) {
			Aspect aspect = ApplicationContextUtils.instantiateClass(ForkJoinJobExecutor.class, dependency.getForkKeys(),
					dependency.getCompletionRate());
			return JobBeanProxyUtils.getBeanProxy(job, aspect);
		}
		return job;
	}
}
