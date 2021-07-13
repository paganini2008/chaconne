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
package indi.atlantis.framework.chaconne.console.service;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.model.JobStat;
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

	public Map<String, JobStateCount> selectJobStateCount(String clusterName) throws Exception {
		JobStateCount[] stateCounts = jobManager.selectJobStateCount(new Query(clusterName));
		return Arrays.stream(stateCounts).collect(Collectors.toMap(t -> t.getJobState().getRepr(), Function.identity()));
	}

	public JobStat[] selectJobStatByDay(String clusterName) throws Exception {
		JobStatQuery query = new JobStatQuery();
		query.setClusterName(clusterName);
		return jobManager.selectJobStatByDay(query);
	}

}
