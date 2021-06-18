/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.chaconne.model.JobLifeCycleParameter;
import indi.atlantis.framework.tridenter.ApplicationInfo;
import indi.atlantis.framework.tridenter.multicast.ApplicationMessageListener;

/**
 * 
 * LifeCycleListenerContainer
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public abstract class LifeCycleListenerContainer implements ApplicationMessageListener {

	private final Set<LifeCycleListener> lifeCycleListeners = Collections.synchronizedNavigableSet(new TreeSet<LifeCycleListener>());

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	public void addListener(LifeCycleListener listener) {
		if (listener != null) {
			lifeCycleListeners.add(listener);
		}
	}

	public void removeListener(LifeCycleListener listener) {
		if (listener != null) {
			lifeCycleListeners.remove(listener);
		}
	}

	public abstract void onChange(JobKey jobKey, JobLifeCycle lifeCycle);

	@Override
	public void onMessage(ApplicationInfo applicationInfo, String id, Object message) {
		final JobLifeCycleParameter jobParam = (JobLifeCycleParameter) message;
		JobKey jobKey = jobParam.getJobKey();
		JobLifeCycle lifeCycle = jobParam.getLifeCycle();
		accept(jobKey, lifeCycle);
	}

	private void accept(JobKey jobKey, JobLifeCycle lifeCycle) {
		switch (lifeCycle) {
		case CREATION:
			for (LifeCycleListener listener : lifeCycleListeners) {
				listener.afterCreation(jobKey);
			}
			break;
		case DELETION:
			for (LifeCycleListener listener : lifeCycleListeners) {
				listener.beforeDeletion(jobKey);
			}
			break;
		case REFRESH:
			for (LifeCycleListener listener : lifeCycleListeners) {
				listener.afterRefresh(jobKey);
			}
			break;
		}
	}

	public int countOfListeners() {
		return lifeCycleListeners.size();
	}

	@Override
	public String getTopic() {
		return LifeCycleListenerContainer.class.getName();
	}

}
