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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.chaconne.model.JobLifeCycleParameter;
import indi.atlantis.framework.tridenter.multicast.ApplicationMulticastGroup;

/**
 * 
 * EmbeddedModeLifeCycleListenerContainer
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class EmbeddedModeLifeCycleListenerContainer extends LifeCycleListenerContainer {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ApplicationMulticastGroup applicationMulticastGroup;

	@Override
	public void onChange(JobKey jobKey, JobLifeCycle lifeCycle) {
		applicationMulticastGroup.multicast(applicationName, LifeCycleListenerContainer.class.getName(),
				new JobLifeCycleParameter(jobKey, lifeCycle));
	}

}
