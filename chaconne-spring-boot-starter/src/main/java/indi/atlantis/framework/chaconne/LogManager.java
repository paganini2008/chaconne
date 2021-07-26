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

import com.github.paganini2008.devtools.ExceptionUtils;

/**
 * 
 * LogManager
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public interface LogManager {

	default void log(long traceId, JobKey jobKey, LogLevel logLevel, String messagePattern, Object[] args, Throwable e) {
		log(traceId, jobKey, logLevel, messagePattern, args, ExceptionUtils.toArray(e));
	}

	void log(long traceId, JobKey jobKey, LogLevel logLevel, String messagePattern, Object[] args, String[] stackTraces);

	default void log(long traceId, LogLevel level, JobKey jobKey, String msg, Throwable e) {
		log(traceId, level, jobKey, msg, ExceptionUtils.toArray(e));
	}

	void log(long traceId, LogLevel level, JobKey jobKey, String msg, String[] stackTraces);

	default void error(long traceId, JobKey jobKey, Throwable e) {
		error(traceId, jobKey, e.getMessage(), ExceptionUtils.toArray(e));
	}

	void error(long traceId, JobKey jobKey, String msg, String[] stackTraces);

}
