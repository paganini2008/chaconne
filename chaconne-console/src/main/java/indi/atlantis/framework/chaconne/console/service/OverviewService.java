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
package indi.atlantis.framework.chaconne.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.model.JobStat;
import indi.atlantis.framework.chaconne.model.JobStatDetail;
import indi.atlantis.framework.chaconne.model.JobStatQuery;
import indi.atlantis.framework.chaconne.model.JobStateCount;
import indi.atlantis.framework.chaconne.model.Query;

/**
 * 
 * OverviewService
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Component
public class OverviewService {

	@Autowired
	private JobManager jobManager;

	public JobStat selectJobStat(String clusterName) throws Exception {
		return jobManager.selectJobStat(new JobStatQuery(clusterName));
	}

	public JobStateCount[] selectJobStateCount(String clusterName) throws Exception {
		return jobManager.selectJobStateCount(new Query(clusterName));
	}

	public JobStatDetail[] selectJobStatByDay(String clusterName) throws Exception {
		JobStatQuery query = new JobStatQuery(clusterName);
		return jobManager.selectJobStatByDay(query);
	}

}
