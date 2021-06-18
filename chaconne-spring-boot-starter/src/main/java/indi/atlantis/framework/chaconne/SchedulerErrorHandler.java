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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ErrorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * SchedulingErrorHandler
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class SchedulerErrorHandler implements ErrorHandler {

	@Autowired
	private ScheduleManager scheduleManager;

	@Autowired
	private JobManager jobManager;

	@Override
	public void handleError(Throwable t) {
		if (t instanceof JobTerminationException) {
			final JobTerminationException cause = (JobTerminationException) t;
			final JobKey jobKey = cause.getJobKey();
			try {
				scheduleManager.unscheduleJob(jobKey);
				jobManager.finishJob(jobKey);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.error(t.getMessage(), t);
		}
	}

}
