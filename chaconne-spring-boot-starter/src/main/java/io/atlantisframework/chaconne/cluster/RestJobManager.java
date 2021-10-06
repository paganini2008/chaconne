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
package io.atlantisframework.chaconne.cluster;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import io.atlantisframework.chaconne.DependencyType;
import io.atlantisframework.chaconne.JobDefinition;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.JobState;
import io.atlantisframework.chaconne.model.JobDependencyParameter;
import io.atlantisframework.chaconne.model.JobDetail;
import io.atlantisframework.chaconne.model.JobKeyQuery;
import io.atlantisframework.chaconne.model.JobLog;
import io.atlantisframework.chaconne.model.JobPersistParameter;
import io.atlantisframework.chaconne.model.JobRuntimeDetail;
import io.atlantisframework.chaconne.model.JobStackTrace;
import io.atlantisframework.chaconne.model.JobStat;
import io.atlantisframework.chaconne.model.JobStatDetail;
import io.atlantisframework.chaconne.model.JobStatPageQuery;
import io.atlantisframework.chaconne.model.JobStatQuery;
import io.atlantisframework.chaconne.model.JobStateCount;
import io.atlantisframework.chaconne.model.JobStateParameter;
import io.atlantisframework.chaconne.model.JobTrace;
import io.atlantisframework.chaconne.model.JobTracePageQuery;
import io.atlantisframework.chaconne.model.JobTraceQuery;
import io.atlantisframework.chaconne.model.JobTriggerDetail;
import io.atlantisframework.chaconne.model.PageQuery;
import io.atlantisframework.chaconne.model.Query;
import io.atlantisframework.chaconne.model.Result;
import io.atlantisframework.chaconne.utils.GenericJobDefinition;

/**
 * 
 * RestJobManager
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class RestJobManager implements JobManager {

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Override
	public String[] selectClusterNames() throws Exception {
		ResponseEntity<Result<String[]>> responseEntity = restTemplate.perform(null, "/job/manager/selectClusterNames", HttpMethod.GET,
				null, new ParameterizedTypeReference<Result<String[]>>() {
				});
		return responseEntity.getBody().getData();
	}
	
	@Override
	public int persistJob(JobDefinition jobDefinition, String attachment) throws Exception {
		if (!(jobDefinition instanceof GenericJobDefinition)) {
			throw new UnsupportedOperationException("Please use GenericJobDefinition.Builder to build a new job.");
		}
		GenericJobDefinition jobDef = (GenericJobDefinition) jobDefinition;
		JobPersistParameter parameter = jobDef.toParameter();
		parameter.setAttachment(attachment);
		return persistJob(parameter);
	}

	@Override
	public int persistJob(JobPersistParameter parameter) throws Exception {
		ResponseEntity<Result<Integer>> responseEntity = restTemplate.perform(parameter.getJobKey().getClusterName(),
				"/job/manager/persistJob", HttpMethod.POST, parameter, new ParameterizedTypeReference<Result<Integer>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobState finishJob(JobKey jobKey) throws Exception {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/finishJob",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public boolean hasJob(JobKey jobKey) throws Exception {
		ResponseEntity<Result<Boolean>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/hasJob",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<Boolean>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobState pauseJob(JobKey jobKey) throws Exception {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/pauseJob",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobState resumeJob(JobKey jobKey) throws Exception {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/resumeJob",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public boolean hasJobState(JobKey jobKey, JobState jobState) throws Exception {
		ResponseEntity<Result<Boolean>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/hasJobState",
				HttpMethod.POST, new JobStateParameter(jobKey, jobState), new ParameterizedTypeReference<Result<Boolean>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobState setJobState(JobKey jobKey, JobState jobState) throws Exception {
		ResponseEntity<Result<JobState>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/setJobState",
				HttpMethod.POST, new JobStateParameter(jobKey, jobState), new ParameterizedTypeReference<Result<JobState>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobDetail getJobDetail(JobKey jobKey, boolean detailed) throws Exception {
		ResponseEntity<Result<JobDetail>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/getJobDetail",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<JobDetail>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobTriggerDetail getJobTriggerDetail(JobKey jobKey) throws Exception {
		ResponseEntity<Result<JobTriggerDetail>> responseEntity = restTemplate.perform(jobKey.getClusterName(),
				"/job/manager/getJobTriggerDetail", HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<JobTriggerDetail>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public boolean hasRelations(JobKey jobKey, DependencyType dependencyType) throws Exception {
		ResponseEntity<Result<Boolean>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/hasRelations",
				HttpMethod.POST, new JobDependencyParameter(jobKey, dependencyType), new ParameterizedTypeReference<Result<Boolean>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobKey[] getRelations(JobKey jobKey, DependencyType dependencyType) throws Exception {
		ResponseEntity<Result<JobKey[]>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/getRelations",
				HttpMethod.POST, new JobDependencyParameter(jobKey, dependencyType), new ParameterizedTypeReference<Result<JobKey[]>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobKey[] getDependentKeys(JobKey jobKey, DependencyType dependencyType) throws Exception {
		ResponseEntity<Result<JobKey[]>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/getDependentKeys",
				HttpMethod.POST, new JobDependencyParameter(jobKey, dependencyType), new ParameterizedTypeReference<Result<JobKey[]>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobKey[] getJobKeys(JobKeyQuery jobQuery) throws Exception {
		ResponseEntity<Result<JobKey[]>> responseEntity = restTemplate.perform(jobQuery.getClusterName(), "/job/manager/getJobKeys",
				HttpMethod.POST, jobQuery, new ParameterizedTypeReference<Result<JobKey[]>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobRuntimeDetail getJobRuntimeDetail(JobKey jobKey) throws Exception {
		ResponseEntity<Result<JobRuntimeDetail>> responseEntity = restTemplate.perform(jobKey.getClusterName(),
				"/job/manager/getJobRuntimeDetail", HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<JobRuntimeDetail>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public int getJobId(JobKey jobKey) throws Exception {
		ResponseEntity<Result<Integer>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/getJobId",
				HttpMethod.POST, jobKey, new ParameterizedTypeReference<Result<Integer>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public void selectJobDetail(PageQuery<JobDetail> pageQuery) throws Exception {
		ResponseEntity<Result<PageQuery<JobDetail>>> responseEntity = restTemplate.perform(pageQuery.getClusterName(),
				"/job/manager/selectJobDetail", HttpMethod.POST, pageQuery, new ParameterizedTypeReference<Result<PageQuery<JobDetail>>>() {
				});
		PageQuery<JobDetail> data = responseEntity.getBody().getData();
		if (data != null) {
			pageQuery.setRows(data.getRows());
			pageQuery.setContent(data.getContent());
			pageQuery.setNextPage(data.isNextPage());
		}
	}

	@Override
	public void selectJobTrace(JobTracePageQuery<JobTrace> pageQuery) throws Exception {
		ResponseEntity<Result<PageQuery<JobTrace>>> responseEntity = restTemplate.perform(pageQuery.getClusterName(),
				"/job/manager/selectJobTrace", HttpMethod.POST, pageQuery, new ParameterizedTypeReference<Result<PageQuery<JobTrace>>>() {
				});
		PageQuery<JobTrace> data = responseEntity.getBody().getData();
		if (data != null) {
			pageQuery.setRows(data.getRows());
			pageQuery.setContent(data.getContent());
			pageQuery.setNextPage(data.isNextPage());
		}
	}

	@Override
	public JobStackTrace[] selectJobStackTrace(JobTraceQuery query) throws SQLException {
		ResponseEntity<Result<JobStackTrace[]>> responseEntity = restTemplate.perform(query.getClusterName(),
				"/job/manager/selectJobStackTrace", HttpMethod.POST, query, new ParameterizedTypeReference<Result<JobStackTrace[]>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobLog[] selectJobLog(JobTraceQuery query) throws SQLException {
		ResponseEntity<Result<JobLog[]>> responseEntity = restTemplate.perform(query.getClusterName(), "/job/manager/selectJobLog",
				HttpMethod.POST, query, new ParameterizedTypeReference<Result<JobLog[]>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobStatDetail[] selectJobStatByDay(JobStatQuery query) throws Exception {
		ResponseEntity<Result<JobStatDetail[]>> responseEntity = restTemplate.perform(query.getClusterName(),
				"/job/manager/selectJobStatByDay", HttpMethod.POST, query, new ParameterizedTypeReference<Result<JobStatDetail[]>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobStatDetail[] selectJobStatByMonth(JobStatQuery query) throws Exception {
		ResponseEntity<Result<JobStatDetail[]>> responseEntity = restTemplate.perform(query.getClusterName(),
				"/job/manager/selectJobStatByMonth", HttpMethod.POST, query, new ParameterizedTypeReference<Result<JobStatDetail[]>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public void selectJobStatById(JobStatPageQuery<JobStatDetail> pageQuery) throws Exception {
		ResponseEntity<Result<PageQuery<JobStatDetail>>> responseEntity = restTemplate.perform(pageQuery.getClusterName(),
				"/job/manager/selectJobStatById", HttpMethod.POST, pageQuery,
				new ParameterizedTypeReference<Result<PageQuery<JobStatDetail>>>() {
				});
		PageQuery<JobStatDetail> data = responseEntity.getBody().getData();
		if (data != null) {
			pageQuery.setRows(data.getRows());
			pageQuery.setContent(data.getContent());
			pageQuery.setNextPage(data.isNextPage());
		}
	}

	@Override
	public JobStateCount[] selectJobStateCount(Query query) throws Exception {
		ResponseEntity<Result<JobStateCount[]>> responseEntity = restTemplate.perform(query.getClusterName(),
				"/job/manager/selectJobStateCount", HttpMethod.POST, query, new ParameterizedTypeReference<Result<JobStateCount[]>>() {
				});
		return responseEntity.getBody().getData();
	}

	@Override
	public JobStat selectJobStat(JobStatQuery query) throws Exception {
		ResponseEntity<Result<JobStat>> responseEntity = restTemplate.perform(query.getClusterName(), "/job/manager/selectJobStat",
				HttpMethod.POST, query, new ParameterizedTypeReference<Result<JobStat>>() {
				});
		return responseEntity.getBody().getData();
	}

}
