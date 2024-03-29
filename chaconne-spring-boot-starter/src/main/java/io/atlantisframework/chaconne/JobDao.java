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
package io.atlantisframework.chaconne;

import java.util.List;
import java.util.Map;

import com.github.paganini2008.springdesert.fastjdbc.annotations.Args;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Batch;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Dao;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Example;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Insert;
import com.github.paganini2008.springdesert.fastjdbc.annotations.Update;

/**
 * 
 * JobDao
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Dao
public interface JobDao {

	public static final String DEF_INSERT_JOB_SERVER_DETAIL = "insert into chac_job_server_detail(cluster_name,group_name,instance_id,leader,context_path,start_date,contact_person,contact_email) values (:clusterName,:groupName,:instanceId,:leader,:contextPath,:startDate,:contactPerson,:contactEmail)";
	public static final String DEF_INSERT_JOB_DETAIL = "insert into chac_job_detail(cluster_name,group_name,job_name,job_class_name,description,attachment,email,retries,weight,timeout,create_date) values (:clusterName,:groupName,:jobName,:jobClassName,:description,:attachment,:email,:retries,:weight,:timeout,:createDate)";
	public static final String DEF_INSERT_JOB_RUNTIME = "insert into chac_job_runtime_detail(job_id, job_state) values (:jobId, :jobState)";
	public static final String DEF_INSERT_JOB_TRACE = "insert into chac_job_trace(trace_id,cluster_name,group_name,job_id,running_state,address,instance_id,completed,failed,skipped,finished,retries,execution_time,completion_time) values (:traceId,:clusterName,:groupName,:jobId,:runningState,:address,:instanceId,:completed,:failed,:skipped,:finished,:retries,:executionTime,:completionTime)";
	public static final String DEF_INSERT_JOB_EXCEPTION = "insert into chac_job_exception(trace_id, job_id, stack_trace) values (:traceId, :jobId, :stackTrace)";
	public static final String DEF_INSERT_JOB_LOG = "insert into chac_job_log(trace_id, job_id, level, log, create_date) values (:traceId, :jobId, :level, :log, :createDate)";
	public static final String DEF_INSERT_JOB_TRIGGER = "insert into chac_job_trigger_detail(job_id, trigger_type, trigger_description, start_date, end_date, repeat_count) values (:jobId, :triggerType, :triggerDescription, :startDate, :endDate, :repeatCount)";
	public static final String DEF_INSERT_JOB_DEPENDENCY = "insert into chac_job_dependency(job_id, dependent_job_id, dependency_type) value (:jobId, :dependentJobId, :dependencyType)";

	public static final String DEF_UPDATE_SERVER_DETAIL ="update chac_job_server_detail set instance_id=:instanceId, leader=:leader, start_date=:startDate, contact_person=:contactPerson, contact_email=:contactEmail where cluster_name=:clusterName and group_name=:groupName and context_path=:contextPath";
	public static final String DEF_UPDATE_JOB_DETAIL = "update chac_job_detail set description=:description, attachment=:attachment, email=:email, retries=:retries, weight=:weight, timeout=:timeout where cluster_name=:clusterName and group_name=:groupName and job_name=:jobName and job_class_name=:jobClassName";
	public static final String DEF_UPDATE_JOB_TRIGGER = "update chac_job_trigger_detail set trigger_type=:triggerType, trigger_description=:triggerDescription, start_date=:startDate, end_date=:endDate, repeat_count=:repeatCount where job_id=:jobId";
	public static final String DEF_UPDATE_JOB_RUNNING_BEGIN = "update chac_job_runtime_detail set job_state=:jobState, last_execution_time=:lastExecutionTime, next_execution_time=:nextExecutionTime where job_id=(select job_id from chac_job_detail where cluster_name=:clusterName and group_name=:groupName and job_name=:jobName and job_class_name=:jobClassName)";
	public static final String DEF_UPDATE_JOB_RUNNING_END = "update chac_job_runtime_detail set job_state=:jobState, last_running_state=:lastRunningState, last_completion_time=:lastCompletionTime where job_id=(select job_id from chac_job_detail where cluster_name=:clusterName and group_name=:groupName and job_name=:jobName and job_class_name=:jobClassName)";
	public static final String DEF_UPDATE_JOB_STATE = "update chac_job_runtime_detail set job_state=:jobState where job_id=:jobId";

	public static final String DEF_DELETE_JOB_DEPENDENCY = "delete from chac_job_dependency where job_id=:jobId and dependency_type=:dependencyType";
	public static final String DEF_CLEAN_JOB_SERVER_DETAIL = "delete from chac_job_server_detail";
	public static final String DEF_DELETE_JOB_SERVER_DETAIL = "delete from chac_job_server_detail where cluster_name=:clusterName and group_name=:groupName and context_path=:contextPath";

	@Insert(DEF_INSERT_JOB_SERVER_DETAIL)
	int saveJobServerDetail(@Example Map<String, Object> kwargs);

	@Insert(DEF_INSERT_JOB_DETAIL)
	int saveJobDetail(@Example Map<String, Object> kwargs);

	@Update(DEF_INSERT_JOB_RUNTIME)
	int saveJobRuntimeDetail(@Example Map<String, Object> kwargs);

	@Update(DEF_INSERT_JOB_TRACE)
	int saveJobTrace(@Example Map<String, Object> kwargs);

	@Batch(DEF_INSERT_JOB_EXCEPTION)
	int saveJobException(@Args List<Map<String, Object>> kwargsList);

	@Update(DEF_INSERT_JOB_LOG)
	int saveJobLog(@Example Map<String, Object> kwargs);

	@Update(DEF_INSERT_JOB_TRIGGER)
	int saveJobTriggerDetail(@Example Map<String, Object> kwargs);

	@Update(DEF_INSERT_JOB_DEPENDENCY)
	int saveJobDependency(@Example Map<String, Object> kwargs);

	@Update(DEF_UPDATE_JOB_DETAIL)
	int updateJobDetail(@Example Map<String, Object> kwargs);
	
	@Update(DEF_UPDATE_SERVER_DETAIL)
	int updateJobServerDetail(@Example Map<String, Object> kwargs);

	@Update(DEF_UPDATE_JOB_TRIGGER)
	int updateJobTrigger(@Example Map<String, Object> kwargs);

	@Update(DEF_UPDATE_JOB_RUNNING_BEGIN)
	int updateJobRunningBegin(@Example Map<String, Object> kwargs);

	@Update(DEF_UPDATE_JOB_RUNNING_END)
	int updateJobRunningEnd(@Example Map<String, Object> kwargs);

	@Update(DEF_UPDATE_JOB_STATE)
	int updateJobState(@Example Map<String, Object> kwargs);

	@Update(DEF_DELETE_JOB_DEPENDENCY)
	int deleteJobDependency(@Example Map<String, Object> kwargs);

	@Update(DEF_CLEAN_JOB_SERVER_DETAIL)
	int cleanJobServerDetail();

	@Update(DEF_DELETE_JOB_SERVER_DETAIL)
	int deleteJobServerDetail(@Example Map<String, Object> kwargs);

}
