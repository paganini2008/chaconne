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

/**
 * 
 * ChaconneBeanNames
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public abstract class ChaconneBeanNames {

	public static final String JOB_SCHEDULER = "chaconne:job-scheduler";

	public static final String MAIN_JOB_EXECUTOR = "chaconne:main-job-executor";

	public static final String TARGET_JOB_EXECUTOR = "chaconne:target-job-executor";

	public static final String INTERNAL_JOB_BEAN_LOADER = "chaconne:internal-job-bean-loader";

	public static final String EXTERNAL_JOB_BEAN_LOADER = "chaconne:external-job-bean-loader";

	public static final String SCHEDULER_ERROR_HANDLER = "chaconne:scheduler-error-handler";
	
	public static final String MAIN_THREAD_POOL = "chaconne:main-thread-pool";

	public static final String EXECUTOR_THREAD_POOL = "chaconne:executor-thread-pool";

}
