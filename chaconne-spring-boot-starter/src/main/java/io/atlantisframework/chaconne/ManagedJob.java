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

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.beans.PropertyFilters;
import com.github.paganini2008.devtools.beans.ToStringBuilder;

/**
 * 
 * ManagedJob
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public abstract class ManagedJob implements Job, BeanNameAware {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Value("${spring.application.name}")
	private String applicationName;

	private String jobName;

	@Override
	public String getClusterName() {
		return clusterName;
	}

	@Override
	public String getGroupName() {
		return applicationName;
	}

	@Override
	public String getJobName() {
		return jobName;
	}

	@Override
	public void setBeanName(String name) {
		this.jobName = name;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				PropertyFilters.includedProperties(new String[] { "clusterName", "groupName", "jobName", "jobClassName" }));
	}

}
