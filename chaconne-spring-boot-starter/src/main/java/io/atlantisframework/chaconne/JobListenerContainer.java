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

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.beans.BeanUtils;

import io.atlantisframework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobListenerContainer
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class JobListenerContainer {

	private final Set<JobListener> globalListeners = Collections.synchronizedNavigableSet(new TreeSet<JobListener>());

	private final Map<JobKey, JobListener> listeners = Collections.synchronizedMap(new TreeMap<JobKey, JobListener>());

	public void addListener(JobKey jobKey, JobListener listener) {
		Assert.isNull(listener, "Nullable JobListener");
		if (jobKey != null) {
			if (!listeners.containsKey(jobKey)) {
				listeners.putIfAbsent(jobKey, listener);
				log.info("Add JobListener '{}' to job '{}'", listener, jobKey);
			}
		} else {
			if (globalListeners.add(listener)) {
				log.info("Add JobListener '{}'", listener);
			}
		}
	}

	public void removeListener(JobKey jobKey, JobListener listener) {
		Assert.isNull(listener, "Nullable JobListener");
		if (jobKey != null) {
			if (listeners.remove(jobKey, listener)) {
				log.info("Remove JobListener '{}' from job '{}'", listener, jobKey);
			}
		} else {
			if (globalListeners.remove(listener)) {
				log.info("Remove JobListener '{}'", listener);
			}
		}
	}

	public void beforeRun(long traceId, JobKey jobKey, Job job, Object attachment, Date startDate) {
		if (log.isTraceEnabled()) {
			log.trace("Trigger all JobListeners on before running job '{}'", jobKey);
		}
		for (JobListener listener : globalListeners) {
			listener.beforeRun(traceId, jobKey, attachment, startDate);
		}

		if (listeners.containsKey(jobKey)) {
			listeners.get(jobKey).beforeRun(traceId, jobKey, attachment, startDate);
		}

		Class<?>[] listenerClasses = job.getJobListeners();
		if (ArrayUtils.isNotEmpty(listenerClasses)) {
			for (Class<?> listenerClass : listenerClasses) {
				JobListener listener = (JobListener) BeanUtils.instantiate(listenerClass);
				listener.beforeRun(traceId, jobKey, attachment, startDate);
			}
		}
	}

	public void afterRun(long traceId, JobKey jobKey, Job job, Object attachment, Date startDate, RunningState runningState, Object result,
			Throwable reason, int retries) {
		if (log.isTraceEnabled()) {
			log.trace("Trigger all JobListeners on after running job '{}'", jobKey);
		}
		Class<?>[] listenerClasses = job.getJobListeners();
		if (ArrayUtils.isNotEmpty(listenerClasses)) {
			for (Class<?> listenerClass : listenerClasses) {
				JobListener listener = (JobListener) ApplicationContextUtils.getOrCreateBean(listenerClass);
				listener.afterRun(traceId, jobKey, attachment, startDate, runningState, result, reason);
			}
		}

		if (listeners.containsKey(jobKey)) {
			listeners.get(jobKey).afterRun(traceId, jobKey, attachment, startDate, runningState, result, reason);
		}

		for (JobListener listener : globalListeners) {
			listener.afterRun(traceId, jobKey, attachment, startDate, runningState, result, reason);
		}
	}

	public int countOfListeners() {
		return globalListeners.size() + listeners.size();
	}

}
