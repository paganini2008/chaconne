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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.Observer;
import com.github.paganini2008.devtools.collection.CollectionUtils;

import io.atlantisframework.tridenter.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SerialDependencyFuture
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class SerialDependencyFuture implements JobFuture {

	private final List<JobKey> dependencies;
	private final List<Observer> observers;
	private final Observable observable;
	private final AtomicBoolean cancelled = new AtomicBoolean();
	private final AtomicBoolean done = new AtomicBoolean();

	SerialDependencyFuture(List<JobKey> dependencies, List<Observer> observers, Observable observable) {
		this.dependencies = dependencies;
		this.observers = observers;
		this.observable = observable;
	}

	@Override
	public void cancel() {
		for (int i = 0; i < dependencies.size(); i++) {
			observable.deleteObserver(dependencies.get(i).getIdentifier(), observers.get(i));
		}
		cancelled.set(true);
		done.set(false);
	}

	@Override
	public boolean isDone() {
		return done.get();
	}

	@Override
	public boolean isCancelled() {
		return cancelled.get();
	}

	public List<JobKey> getDependencies() {
		return dependencies;
	}

	public List<Observer> getObservers() {
		return observers;
	}

	@Override
	public long getNextExectionTime(Date lastExecutionTime, Date lastActualExecutionTime, Date lastCompletionTime) {
		JobManager jobManager = ApplicationContextUtils.getBean(JobManager.class);
		List<Integer> jobIds = new ArrayList<Integer>();
		try {
			for (JobKey jobKey : dependencies) {
				if (jobManager.hasJob(jobKey)) {
					jobIds.add(jobManager.getJobId(jobKey));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return NEXT_EXECUTION_TIME_NOT_FOUND;
		}
		if (jobIds.isEmpty()) {
			return NEXT_EXECUTION_TIME_NOT_FOUND;
		}
		JobQueryDao jobQueryDao = ApplicationContextUtils.getBean(JobQueryDao.class);
		Date latestDate = jobQueryDao.selectLatestExecutionTime(CollectionUtils.join(jobIds, ","));
		return latestDate != null ? latestDate.getTime() : NEXT_EXECUTION_TIME_NOT_FOUND;
	}

}
