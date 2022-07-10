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

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import io.atlantisframework.chaconne.model.JobDetail;

/**
 * 
 * JobRunningLimitationNotification
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class JobRunningLimitationNotification extends JobConditionalTermination {

	@Autowired
	private JobQueryDao jobQueryDao;

	@Autowired
	private JobManager jobManager;

	@Override
	protected boolean apply(long traceId, JobKey jobKey, Object attachment, Date startDate) {
		JobDetail jobDetail;
		try {
			jobDetail = jobManager.getJobDetail(jobKey, true);
		} catch (Exception e) {
			throw ExceptionUtils.wrapExeception(e);
		}
		int repeatCount = jobDetail.getJobTriggerDetail().getRepeatCount();
		if (repeatCount > 0) {
			int runningCount = jobQueryDao.selectJobRunningCount(jobDetail.getJobId());
			return runningCount >= repeatCount;
		}
		return false;
	}

}
