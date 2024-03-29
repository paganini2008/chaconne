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

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import io.atlantisframework.tridenter.LeaderState;
import io.atlantisframework.tridenter.election.ApplicationClusterLeaderEvent;
import io.atlantisframework.tridenter.utils.BeanLifeCycle;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DefaultSchedulerStarterListener
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class DefaultSchedulerStarterListener
		implements ApplicationListener<ApplicationClusterLeaderEvent>, Executable, SchedulerStarterListener, BeanLifeCycle {

	@Autowired
	private ScheduleManager scheduleManager;

	@Autowired(required = false)
	private JobBeanInitializer jobBeanInitializer;

	@Value("${atlantis.framework.chaconne.scheduler.starter.inititalDelay:5}")
	private int inititalDelay;

	@Value("${atlantis.framework.chaconne.scheduler.starter.checkInterval:60}")
	private int checkInterval;

	private Timer timer;

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		if (event.getLeaderState() == LeaderState.UP) {
			timer = ThreadUtils.scheduleWithFixedDelay(this, inititalDelay, checkInterval, TimeUnit.SECONDS);
		}
	}

	@Override
	public boolean execute() {
		if (jobBeanInitializer != null) {
			try {
				jobBeanInitializer.initializeJobBeans();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		scheduleManager.doSchedule();
		return jobBeanInitializer != null;
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
