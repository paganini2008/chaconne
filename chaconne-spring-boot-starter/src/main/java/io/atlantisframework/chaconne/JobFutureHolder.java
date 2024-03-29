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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.atlantisframework.tridenter.utils.BeanLifeCycle;

/**
 * 
 * JobFutureHolder
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class JobFutureHolder implements BeanLifeCycle {

	private final Map<JobKey, JobFuture> cache = new ConcurrentHashMap<JobKey, JobFuture>();

	public void add(JobKey jobKey, JobFuture jobFuture) {
		cache.put(jobKey, jobFuture);
	}

	public JobFuture get(JobKey jobKey) {
		return cache.get(jobKey);
	}

	public boolean hasKey(JobKey jobKey) {
		return cache.containsKey(jobKey);
	}

	public void cancel(JobKey jobKey) {
		JobFuture jobFuture = cache.remove(jobKey);
		if (jobFuture != null) {
			jobFuture.cancel();
		}
	}

	public int size() {
		return cache.size();
	}

	public void clear() {
		for (Map.Entry<JobKey, JobFuture> entry : cache.entrySet()) {
			entry.getValue().cancel();
		}
		cache.clear();
	}

	@Override
	public void destroy() {
		clear();
	}

}
