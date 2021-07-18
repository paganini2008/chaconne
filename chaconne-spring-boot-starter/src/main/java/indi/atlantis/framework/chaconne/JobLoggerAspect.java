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
package indi.atlantis.framework.chaconne;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.Marker;

import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.proxy.Aspect;

/**
 * 
 * JobLoggerAspect
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class JobLoggerAspect implements Aspect {

	private static final List<String> enhancedMethodNames = Collections
			.unmodifiableList(Arrays.asList("trace", "debug", "info", "warn", "error"));

	private final JobKey jobKey;
	private final LogManager logManager;

	JobLoggerAspect(JobKey jobKey, LogManager logManager) {
		this.jobKey = jobKey;
		this.logManager = logManager;
	}

	private long traceId;

	public void setTraceId(long traceId) {
		this.traceId = traceId;
	}

	@Override
	public boolean afterCall(Object target, Method method, Object[] args) {
		final Logger log = (Logger) target;
		final String methodName = method.getName();
		if (enhancedMethodNames.contains(methodName)) {
			List<Object> list = new ArrayList<Object>(Arrays.asList(args));
			Object firstArg = list.remove(0);
			Marker marker = firstArg instanceof Marker ? (Marker) firstArg : null;
			LogLevel logLevel = LogLevel.valueOf(methodName.toUpperCase());
			boolean canLog = marker != null ? logLevel.canLog(log, marker) : logLevel.canLog(log);
			if (canLog) {
				try {
					String messagePattern = marker != null ? (String) list.remove(1) : (String) firstArg;
					Throwable cause = null;
					if (list.size() > 0) {
						Object lastArg = ListUtils.getLast(list);
						if (lastArg instanceof Throwable) {
							cause = (Throwable) lastArg;
							ListUtils.removeLast(list);
						}
					}
					logManager.log(traceId, jobKey, logLevel, messagePattern, list.toArray(), cause);
				} catch (Exception ignored) {
				}
			}
		}
		return true;
	}

}
