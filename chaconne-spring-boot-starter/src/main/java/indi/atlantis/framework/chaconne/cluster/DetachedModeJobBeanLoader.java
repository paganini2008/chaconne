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
package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.chaconne.Job;
import indi.atlantis.framework.chaconne.JobBeanLoader;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.model.JobTriggerDetail;
import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;

/**
 * 
 * DetachedModeJobBeanLoader
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class DetachedModeJobBeanLoader implements JobBeanLoader {

	@Autowired
	private JobManager jobManager;

	@Override
	public Job loadJobBean(JobKey jobKey) throws Exception {
		final JobTriggerDetail triggerDetail = jobManager.getJobTriggerDetail(jobKey);
		return ApplicationContextUtils.autowireBean(new DetachedModeJobBeanProxy(jobKey, triggerDetail));

	}

}
