/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.chaconne.JobDependencyUpdater;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.TriggerType;
import indi.atlantis.framework.chaconne.model.JobKeyQuery;

/**
 * 
 * RemoteJobDependencyUpdater
 * 
 * @author Fred Feng
 *
 * @version 1.0
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
