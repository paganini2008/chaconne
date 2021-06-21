package indi.atlantis.framework.chaconne.cluster;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import indi.atlantis.framework.chaconne.DependencyType;
import indi.atlantis.framework.chaconne.JobDefinition;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobManager;
import indi.atlantis.framework.chaconne.JobState;
import indi.atlantis.framework.chaconne.model.JobDependencyParameter;
import indi.atlantis.framework.chaconne.model.JobDetail;
import indi.atlantis.framework.chaconne.model.JobKeyQuery;
import indi.atlantis.framework.chaconne.model.JobLog;
import indi.atlantis.framework.chaconne.model.JobPersistParameter;
import indi.atlantis.framework.chaconne.model.Result;
import indi.atlantis.framework.chaconne.model.JobRuntimeDetail;
import indi.atlantis.framework.chaconne.model.JobStackTrace;
import indi.atlantis.framework.chaconne.model.JobStateParameter;
import indi.atlantis.framework.chaconne.model.JobTrace;
import indi.atlantis.framework.chaconne.model.JobTracePageQuery;
import indi.atlantis.framework.chaconne.model.JobTraceQuery;
import indi.atlantis.framework.chaconne.model.JobTriggerDetail;
import indi.atlantis.framework.chaconne.model.PageQuery;
import indi.atlantis.framework.chaconne.utils.GenericJobDefinition;

/**
 * 
 * RestJobManager
 * 
 * @author Fred Feng
 *
 * @since 1.0
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
	public int persistJob(JobPersistParameter parameter) throws Exception {
		ResponseEntity<Result<Integer>> responseEntity = restTemplate.perform(parameter.getJobKey().getClusterName(),
				"/job/manager/persistJob", HttpMethod.POST, parameter, new ParameterizedTypeReference<Result<Integer>>() {
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
		throw new UnsupportedOperationException("pauseJob");
	}

	@Override
	public JobState resumeJob(JobKey jobKey) throws Exception {
		throw new UnsupportedOperationException("resumeJob");
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
				"/job/manager/selectJobDetail", HttpMethod.POST, pageQuery,
				new ParameterizedTypeReference<Result<PageQuery<JobDetail>>>() {
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
				"/job/manager/selectJobTrace", HttpMethod.POST, pageQuery,
				new ParameterizedTypeReference<Result<PageQuery<JobTrace>>>() {
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

}
