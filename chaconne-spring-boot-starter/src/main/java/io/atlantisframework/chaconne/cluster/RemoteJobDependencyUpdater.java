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
package io.atlantisframework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.StringUtils;

import io.atlantisframework.chaconne.JobDependencyUpdater;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.TriggerType;
import io.atlantisframework.chaconne.model.JobKeyQuery;

/**
 * 
 * RemoteJobDependencyUpdater
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class RemoteJobDependencyUpdater extends JobDependencyUpdater {

	@Value("${atlantis.framework.chaconne.producer.job.clusterNames:}")
	private String clusterNames;

	@Value("${atlantis.framework.chaconne.producer.job.groupNames:}")
	private String groupNames;

	@Autowired
	private JobManager jobManager;

	@Override
	protected JobKey[] selectDependentKeys() throws Exception {
		JobKeyQuery jobQuery = new JobKeyQuery();
		jobQuery.setTriggerType(TriggerType.DEPENDENT);
		if (StringUtils.isNotBlank(clusterNames)) {
			jobQuery.setClusterNames(clusterNames);
		} else if (StringUtils.isNotBlank(groupNames)) {
			jobQuery.setGroupNames(groupNames);
		}
		return jobManager.getJobKeys(jobQuery);
	}

}
