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
package io.atlantisframework.chaconne;

import java.util.Date;

/**
 * 
 * Scheduler
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public interface Scheduler {

	JobFuture schedule(Job job, Object attachment, Date startDate);

	JobFuture schedule(Job job, Object attachment, String cronExpression);

	JobFuture schedule(Job job, Object attachment, String cronExpression, Date startDate);

	JobFuture scheduleWithFixedDelay(Job job, Object attachment, long period, Date startDate);

	JobFuture scheduleAtFixedRate(Job job, Object attachment, long period, Date startDate);

	JobFuture scheduleWithDependency(Job job, JobKey[] dependencies);

	JobFuture scheduleWithDependency(Job job, JobKey[] dependencies, Date startDate);

	void runJob(Job job, Object attachment);

}
