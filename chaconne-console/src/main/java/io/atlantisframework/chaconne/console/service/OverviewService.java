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
package io.atlantisframework.chaconne.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.model.JobStat;
import io.atlantisframework.chaconne.model.JobStatDetail;
import io.atlantisframework.chaconne.model.JobStatQuery;
import io.atlantisframework.chaconne.model.JobStateCount;
import io.atlantisframework.chaconne.model.Query;

/**
 * 
 * OverviewService
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
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
