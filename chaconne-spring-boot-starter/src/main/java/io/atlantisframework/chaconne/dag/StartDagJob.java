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
package io.atlantisframework.chaconne.dag;

import org.slf4j.Logger;

import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.NotManagedJob;

/**
 * 
 * StartDagJob
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class StartDagJob implements NotManagedJob {

	@Override
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		Context context = new Context();
		context.setAttribute("attachment", attachment);
		return context;
	}

}
