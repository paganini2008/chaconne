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
package indi.atlantis.framework.chaconne.model;

import org.springframework.lang.Nullable;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.RunningState;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobResult
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Getter
@Setter
public class JobResult {

	private JobKey jobKey;
	private Object attachment;
	private @Nullable RunningState runningState;
	private @Nullable Object result;
	private @Nullable JobResult[] forkJobResults;

	public JobResult() {
	}

	public JobResult(JobKey jobKey, Object attachment, RunningState runningState, Object result) {
		this.jobKey = jobKey;
		this.attachment = attachment;
		this.runningState = runningState;
		this.result = result;
	}

}
