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

import io.atlantisframework.chaconne.model.JobDetail;
import io.atlantisframework.chaconne.model.JobKeyQuery;
import io.atlantisframework.chaconne.model.JobLog;
import io.atlantisframework.chaconne.model.JobRuntimeDetail;
import io.atlantisframework.chaconne.model.JobStackTrace;
import io.atlantisframework.chaconne.model.JobStat;
import io.atlantisframework.chaconne.model.JobStatDetail;
import io.atlantisframework.chaconne.model.JobStatPageQuery;
import io.atlantisframework.chaconne.model.JobStatQuery;
import io.atlantisframework.chaconne.model.JobStateCount;
import io.atlantisframework.chaconne.model.JobTrace;
import io.atlantisframework.chaconne.model.JobTracePageQuery;
import io.atlantisframework.chaconne.model.JobTraceQuery;
import io.atlantisframework.chaconne.model.JobTriggerDetail;
import io.atlantisframework.chaconne.model.PageQuery;
import io.atlantisframework.chaconne.model.Query;

/**
 * 
 * JobManager
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface JobManager extends JobPersistence {

	String[] selectClusterNames() throws Exception;

	JobDetail getJobDetail(JobKey jobKey, boolean detailed) throws Exception;

	JobTriggerDetail getJobTriggerDetail(JobKey jobKey) throws Exception;

	boolean hasRelations(JobKey jobKey, DependencyType dependencyType) throws Exception;

	JobKey[] getRelations(JobKey jobKey, DependencyType dependencyType) throws Exception;

	JobKey[] getDependentKeys(JobKey jobKey, DependencyType dependencyType) throws Exception;

	JobKey[] getJobKeys(JobKeyQuery jobQuery) throws Exception;

	int getJobId(JobKey jobKey) throws Exception;

	JobRuntimeDetail getJobRuntimeDetail(JobKey jobKey) throws Exception;

	void selectJobDetail(PageQuery<JobDetail> pageQuery) throws Exception;

	void selectJobTrace(JobTracePageQuery<JobTrace> pageQuery) throws Exception;

	JobStackTrace[] selectJobStackTrace(JobTraceQuery query) throws Exception;

	JobLog[] selectJobLog(JobTraceQuery query) throws Exception;

	JobStatDetail[] selectJobStatByDay(JobStatQuery query) throws Exception;
	
	JobStatDetail[] selectJobStatByMonth(JobStatQuery query) throws Exception;

	void selectJobStatById(JobStatPageQuery<JobStatDetail> query) throws Exception;

	JobStat selectJobStat(JobStatQuery query) throws Exception;

	JobStateCount[] selectJobStateCount(Query query) throws Exception;

}
