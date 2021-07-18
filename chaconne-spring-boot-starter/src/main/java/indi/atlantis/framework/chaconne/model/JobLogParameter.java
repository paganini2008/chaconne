/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.LogLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobLogParameter
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobLogParameter {

	private long traceId;
	private JobKey jobKey;
	private LogLevel logLevel;
	private String messagePattern;
	private Object[] args;
	private String[] stackTraces;

	public JobLogParameter() {
	}

	public JobLogParameter(long traceId, JobKey jobKey, LogLevel logLevel, String messagePattern, Object[] args, String[] stackTraces) {
		this.traceId = traceId;
		this.jobKey = jobKey;
		this.logLevel = logLevel;
		this.messagePattern = messagePattern;
		this.args = args;
		this.stackTraces = stackTraces;
	}

	public JobLogParameter(long traceId, JobKey jobKey, String[] stackTraces) {
		this.traceId = traceId;
		this.jobKey = jobKey;
		this.stackTraces = stackTraces;
	}

}
