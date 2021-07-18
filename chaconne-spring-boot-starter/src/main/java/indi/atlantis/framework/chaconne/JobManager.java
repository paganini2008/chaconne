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
package indi.atlantis.framework.chaconne;

import indi.atlantis.framework.chaconne.model.JobDetail;
import indi.atlantis.framework.chaconne.model.JobKeyQuery;
import indi.atlantis.framework.chaconne.model.JobLog;
import indi.atlantis.framework.chaconne.model.JobRuntimeDetail;
import indi.atlantis.framework.chaconne.model.JobStackTrace;
import indi.atlantis.framework.chaconne.model.JobStat;
import indi.atlantis.framework.chaconne.model.JobStatDetail;
import indi.atlantis.framework.chaconne.model.JobStatPageQuery;
import indi.atlantis.framework.chaconne.model.JobStatQuery;
import indi.atlantis.framework.chaconne.model.JobStateCount;
import indi.atlantis.framework.chaconne.model.JobTrace;
import indi.atlantis.framework.chaconne.model.JobTracePageQuery;
import indi.atlantis.framework.chaconne.model.JobTraceQuery;
import indi.atlantis.framework.chaconne.model.JobTriggerDetail;
import indi.atlantis.framework.chaconne.model.PageQuery;
import indi.atlantis.framework.chaconne.model.Query;

/**
 * 
 * JobManager
 *
 * @author Fred Feng
 * @since 1.0
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

	void selectJobStatById(JobStatPageQuery<JobStatDetail> query) throws Exception;

	JobStat selectJobStat(Query query) throws Exception;

	JobStateCount[] selectJobStateCount(Query query) throws Exception;

}
