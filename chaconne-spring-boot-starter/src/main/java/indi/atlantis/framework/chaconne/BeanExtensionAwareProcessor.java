/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.github.paganini2008.devtools.beans.BeanInstantiationException;

/**
 * 
 * BeanExtensionAwareProcessor
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class BeanExtensionAwareProcessor implements BeanPostProcessor {

	@Autowired
	private LifeCycleListenerContainer lifeCycleListenerContainer;

	@Autowired
	private JobListenerContainer jobListenerContainer;

	@Autowired
	private JobManager jobManager;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Job) {
			Job jobBean = (Job) bean;
			try {
				jobManager.persistJob(jobBean, null);
			} catch (Exception e) {
				throw new BeanInstantiationException(e);
			}
		}
		if (bean instanceof LifeCycleListener) {
			LifeCycleListener listener = (LifeCycleListener) bean;
			lifeCycleListenerContainer.addListener(listener);
		}
		if (bean instanceof JobListener) {
			JobListener listener = (JobListener) bean;
			JobKey jobKey = bean instanceof JobDefinition ? JobKey.of((JobDefinition) bean) : null;
			jobListenerContainer.addListener(jobKey, listener);
		}
		return bean;
	}

}
