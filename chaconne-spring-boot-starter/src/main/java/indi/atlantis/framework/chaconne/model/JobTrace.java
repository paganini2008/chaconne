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

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import indi.atlantis.framework.chaconne.RunningState;
import lombok.Getter;

/**
 * 
 * JobTrace
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Getter
public class JobTrace implements Serializable {

	private static final long serialVersionUID = 1886119510627026178L;
	private long traceId;
	private String address;
	private String instanceId;
	private RunningState runningState;
	private int completed;
	private int failed;
	private int skipped;
	private int retries;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date executionTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date completionTime;

	public void setTraceId(long traceId) {
		this.traceId = traceId;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public void setRunningState(int runningState) {
		this.runningState = RunningState.valueOf(runningState);
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public void setExecutionTime(Date executionTime) {
		this.executionTime = executionTime;
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

}
