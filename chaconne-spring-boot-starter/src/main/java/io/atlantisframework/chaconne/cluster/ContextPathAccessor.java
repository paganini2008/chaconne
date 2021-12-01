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
package io.atlantisframework.chaconne.cluster;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.time.DateUtils;

import io.atlantisframework.tridenter.utils.BeanLifeCycle;

/**
 * 
 * ContextPathAccessor
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class ContextPathAccessor implements Executable, BeanLifeCycle {

	private static final long DEFAULT_CHECK_WINDOW_TIME = DateUtils.convertToMillis(1, TimeUnit.MINUTES);
	private final Map<String, Long> contextPaths = new ConcurrentHashMap<String, Long>();
	private Timer timer;
	private long checkWindowTime = DEFAULT_CHECK_WINDOW_TIME;

	public void setCheckWindowTime(long checkWindowTime) {
		this.checkWindowTime = checkWindowTime;
	}

	public void watchContextPath(String contextPath) {
		if (StringUtils.isNotBlank(contextPath)) {
			contextPaths.putIfAbsent(contextPath, System.currentTimeMillis());
		}
	}

	public boolean canAccess(String contextPath) {
		return !contextPaths.containsKey(contextPath);
	}

	@Override
	public void configure() throws Exception {
		timer = ThreadUtils.scheduleWithFixedDelay(this, 5, TimeUnit.SECONDS);
	}

	@Override
	public void destroy() {
		contextPaths.clear();
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public boolean execute() throws Throwable {
		for (Map.Entry<String, Long> entry : contextPaths.entrySet()) {
			if (System.currentTimeMillis() - entry.getValue() > checkWindowTime) {
				contextPaths.remove(entry.getKey());
			}
		}
		return true;
	}

}
