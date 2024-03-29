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
package io.atlantisframework.chaconne.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.paganini2008.devtools.beans.ToStringBuilder;

import io.atlantisframework.chaconne.JobState;
import io.atlantisframework.chaconne.RunningState;
import lombok.Getter;

/**
 * 
 * JobRuntimeDetail
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@JsonInclude(value = Include.NON_NULL)
@Getter
public class JobRuntimeDetail implements Serializable {

	private static final long serialVersionUID = -6283587791317006889L;
	private int jobId;
	private JobState jobState;
	private RunningState lastRunningState;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastExecutionTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastCompletionTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date nextExecutionTime;

	public JobRuntimeDetail() {
	}

	public void setJobState(int jobState) {
		this.jobState = JobState.valueOf(jobState);
	}

	public void setLastRunningState(int runningState) {
		this.lastRunningState = RunningState.valueOf(runningState);
	}

	public void setLastExecutionTime(Date lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
	}

	public void setLastCompletionTime(Date lastCompletionTime) {
		this.lastCompletionTime = lastCompletionTime;
	}

	public void setNextExecutionTime(Date nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
