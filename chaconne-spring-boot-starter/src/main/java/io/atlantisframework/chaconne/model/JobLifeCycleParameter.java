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
package io.atlantisframework.chaconne.model;

import com.github.paganini2008.devtools.beans.ToStringBuilder;

import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobLifeCycle;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobLifeCycleParameter
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Getter
@Setter
public class JobLifeCycleParameter {

	private JobKey jobKey;
	private JobLifeCycle lifeCycle;

	public JobLifeCycleParameter() {
	}

	public JobLifeCycleParameter(JobKey jobKey, JobLifeCycle lifeCycle) {
		this.jobKey = jobKey;
		this.lifeCycle = lifeCycle;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
