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

/**
 * 
 * JobDefinition
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public interface JobDefinition {

	String getClusterName();

	String getGroupName();

	default String getJobName() {
		String simpleName = getClass().getSimpleName();
		return simpleName.substring(0, 1).toLowerCase().concat(simpleName.substring(1));
	}

	default String getJobClassName() {
		return getClass().getName();
	}

	default Trigger getTrigger() {
		return new NoneTrigger();
	}

	default String getDescription() {
		return null;
	}

	default int getRetries() {
		return 0;
	}

	default int getWeight() {
		return 0;
	}

	default long getTimeout() {
		return -1L;
	}

	default String getEmail() {
		return null;
	}

	default JobKey[] getDependentKeys() {
		return new JobKey[0];
	}

	default JobKey[] getForkKeys() {
		return new JobKey[0];
	}

	default float getCompletionRate() {
		return -1F;
	}

}
