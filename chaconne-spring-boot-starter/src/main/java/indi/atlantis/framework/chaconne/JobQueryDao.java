package indi.atlantis.framework.chaconne;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;
import com.github.paganini2008.springworld.jdbc.annotations.Arg;
import com.github.paganini2008.springworld.jdbc.annotations.Dao;
import com.github.paganini2008.springworld.jdbc.annotations.Example;
import com.github.paganini2008.springworld.jdbc.annotations.Get;
import com.github.paganini2008.springworld.jdbc.annotations.Query;
import com.github.paganini2008.springworld.jdbc.annotations.Select;
import com.github.paganini2008.springworld.jdbc.annotations.Sql;

/**
 * 
 * JobQueryDao
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Dao
public interface JobQueryDao {

	public static final String DEF_SELECT_CLUSTER_NAME = "select distinct cluster_name from chac_job_server_detail";
	public static final String DEF_SELECT_JOB_SERVER_DETAIL = "select * from chac_job_server_detail where cluster_name=:clusterName";
	public static final String DEF_SELECT_CONTEXT_PATH = "select distinct context_path from chac_job_server_detail where cluster_name=:clusterName";
	public static final String DEF_SELECT_ALL_JOB_DETAIL = "select * from chac_job_detail";
	public static final String DEF_SELECT_JOB_DETAIL_BY_CLUSTER_NAMES = "select * from chac_job_detail where cluster_name in (:clusterNames)";
	public static final String DEF_SELECT_JOB_DETAIL_BY_GROUP_NAMES = "select * from chac_job_detail where group_name in (:groupNames)";
	public static final String DEF_SELECT_AVAILABLE_JOB_DETAIL = "select a.* from chac_job_detail a join chac_job_runtime_detail b on a.job_id=b.job_id where b.job_state<4";
	public static final String DEF_SELECT_JOB_ID = "select job_id from chac_job_detail where cluster_name=:clusterName and group_name=:groupName and job_name=:jobName and job_class_name=:jobClassName";
	public static final String DEF_SELECT_JOB_TRIGGER_DEADLINE = "select c.cluster_name,c.group_name,c.job_name,c.job_class_name,a.* from chac_job_trigger_detail a join chac_job_runtime_detail b on a.job_id=b.job_id join chac_job_detail c on c.job_id=b.job_id where a.end_date is not null and b.job_state<4";
	public static final String DEF_SELECT_JOB_KEYS_BY_TRIGGER_TYPE = "select * from chac_job_detail a where 1=1 @sql and exists (select job_id from chac_job_trigger_detail where job_id=a.job_id and trigger_type=:triggerType)";
	public static final String DEF_SELECT_JOB_DETAIL_BY_GROUP_NAME = "select a.* from chac_job_detail a join chac_job_runtime_detail b on a.job_id=b.job_id where a.cluster_name=:clusterName and a.group_name=:groupName and b.job_state<4";
	public static final String DEF_SELECT_JOB_DETAIL_BY_OTHER_GROUP_NAME = "select a.* from chac_job_detail a join chac_job_runtime_detail b on a.job_id=b.job_id where a.cluster_name=:clusterName and a.group_name!=:groupName and b.job_state<4";
	public static final String DEF_SELECT_JOB_EXISTS = "select count(*) from chac_job_detail where cluster_name=:clusterName and group_name=:groupName and job_name=:jobName and job_class_name=:jobClassName";
	public static final String DEF_SELECT_JOB_TRIGGER = "select * from chac_job_trigger_detail where job_id=:jobId";
	public static final String DEF_SELECT_LATEST_EXECUTION_TIME = "select min(next_execution_time) from chac_job_runtime_detail where next_execution_time is not null and job_id in (:jobIds)";
	public static final String DEF_SELECT_JOB_RUNTIME = "select * from chac_job_runtime_detail where job_id=:jobId";
	public static final String DEF_SELECT_JOB_RUNTIME_BY_JOB_STATE = "select a.*,b.* from chac_job_detail a join chac_job_runtime_detail b on a.job_id=b.job_id where a.cluster_name=:clusterName and b.job_state=:jobState";
	public static final String DEF_SELECT_DEPENDENT_JOB_DETAIL = "select * from chac_job_detail where job_id in (select dependent_job_id from chac_job_dependency where job_id=:jobId and dependency_type=:dependencyType)";
	public static final String DEF_SELECT_JOB_HAS_RELATIONS = "select count(*) from chac_job_dependency where dependent_job_id=:dependentJobId and dependency_type=:dependencyType";
	public static final String DEF_SELECT_JOB_RELATIONS = "select * from chac_job_detail where job_id in (select job_id from chac_job_dependency where dependent_job_id=:dependentJobId and dependency_type=:dependencyType)";

	public static final String DEF_SELECT_JOB_TRACE = "select * from chac_job_trace where job_id=:jobId and execution_time between :startDate and :endDate";
	public static final String DEF_SELECT_JOB_RUNNING_COUNT = "select count(1) from chac_job_trace where job_id=:jobId and running_state=1";
	public static final String DEF_SELECT_JOB_INFO = "select a.*,b.job_state,b.last_running_state,b.last_execution_time,b.last_completion_time,b.next_execution_time,c.trigger_type,c.trigger_description,c.start_date,c.end_date from chac_job_detail a join chac_job_runtime_detail b on b.job_id=a.job_id join chac_job_trigger_detail c on c.job_id=b.job_id where a.cluster_name=:clusterName order by create_date desc";
	public static final String DEF_SELECT_JOB_DETAIL = "select * from chac_job_detail where cluster_name=:clusterName and group_name=:groupName and job_name=:jobName and job_class_name=:jobClassName limit 1";
	public static final String DEF_SELECT_JOB_LOG = "select * from chac_job_log where job_id=:jobId and trace_id=:traceId";
	public static final String DEF_SELECT_JOB_EXCEPTION = "select * from chac_job_exception where job_id=:jobId and trace_id=:traceId";
	public static final String DEF_SELECT_JOB_STAT = "select job_id, sum(complete) as completeCount, sum(failed) as failedCount, sum(skipped) as skippedCount {0} from chac_job_trace group by job_id {1}";

	@Query(value = DEF_SELECT_CLUSTER_NAME, singleColumn = true)
	List<String> selectClusterNames();

	@Query(DEF_SELECT_JOB_SERVER_DETAIL)
	List<Map<String, Object>> selectJobServerDetail(@Arg("clusterName") String clusterName);

	@Query(value = DEF_SELECT_CONTEXT_PATH, singleColumn = true)
	List<String> selectContextPath(@Arg("clusterName") String clusterName);

	@Select(DEF_SELECT_ALL_JOB_DETAIL)
	ResultSetSlice<Map<String, Object>> selectAllJobDetails();

	@Select(DEF_SELECT_JOB_DETAIL_BY_CLUSTER_NAMES)
	ResultSetSlice<Map<String, Object>> selectJobDetailsByClusterNames(@Arg("clusterNames") String clusterNames);

	@Select(DEF_SELECT_JOB_DETAIL_BY_GROUP_NAMES)
	ResultSetSlice<Map<String, Object>> selectJobDetailsByGroupNames(@Arg("groupNames") String groupNames);

	@Query(DEF_SELECT_AVAILABLE_JOB_DETAIL)
	List<Map<String, Object>> selectAvailableJobDetails();

	@Get(value = DEF_SELECT_JOB_ID, javaType = true)
	Integer selectJobId(@Arg("clusterName") String clusterName, @Arg("groupName") String groupName, @Arg("jobName") String jobName,
			@Arg("jobClassName") String jobClassName);

	@Query(DEF_SELECT_JOB_TRIGGER_DEADLINE)
	List<Map<String, Object>> selectJobTriggerDeadlines();

	@Query(DEF_SELECT_JOB_KEYS_BY_TRIGGER_TYPE)
	List<Map<String, Object>> selectJobKeysByTriggerType(@Sql String whereClause, @Example Map<String, Object> kwargs,
			@Arg("triggerType") int triggerType);

	@Query(DEF_SELECT_JOB_DETAIL_BY_GROUP_NAME)
	List<Map<String, Object>> selectJobDetailsByGroupName(@Arg("clusterName") String clusterName, @Arg("groupName") String groupName);

	@Query(DEF_SELECT_JOB_DETAIL_BY_OTHER_GROUP_NAME)
	List<Map<String, Object>> selectJobDetailsByOtherGroupName(@Arg("clusterName") String clusterName, @Arg("groupName") String groupName);

	@Get(value = DEF_SELECT_JOB_EXISTS, javaType = true)
	Integer selectJobExists(@Arg("clusterName") String clusterName, @Arg("groupName") String groupName, @Arg("jobName") String jobName,
			@Arg("jobClassName") String jobClassName);

	@Get(DEF_SELECT_JOB_TRIGGER)
	Map<String, Object> selectJobTriggerDetail(@Arg("jobId") int jobId);

	@Get(value = DEF_SELECT_LATEST_EXECUTION_TIME, javaType = true)
	Date selectLatestExecutionTime(@Arg("jobIds") String jobIds);

	@Get(DEF_SELECT_JOB_RUNTIME)
	Map<String, Object> selectJobRuntime(@Arg("jobId") int jobId);

	@Query(DEF_SELECT_JOB_RUNTIME_BY_JOB_STATE)
	List<Map<String, Object>> selectJobRuntimeByJobState(@Arg("clusterName") String clusterName, @Arg("jobState") int jobState);

	@Query(DEF_SELECT_DEPENDENT_JOB_DETAIL)
	List<Map<String, Object>> selectDependentJobDetail(@Arg("jobId") int jobId, @Arg("dependencyType") int dependencyType);

	@Get(value = DEF_SELECT_JOB_HAS_RELATIONS, javaType = true)
	Integer selectJobHasRelations(@Arg("dependentJobId") int dependentJobId, @Arg("dependencyType") int dependencyType);

	@Query(DEF_SELECT_JOB_RELATIONS)
	List<Map<String, Object>> selectJobRelations(@Arg("dependentJobId") int dependentJobId, @Arg("dependencyType") int dependencyType);

	@Select(DEF_SELECT_JOB_TRACE)
	ResultSetSlice<Map<String, Object>> selectJobTrace(@Arg("jobId") int jobId, @Arg("startDate") Date startDate,
			@Arg("endDate") Date endDate);

	@Get(value = DEF_SELECT_JOB_RUNNING_COUNT, javaType = true)
	Integer selectJobRunningCount(@Arg("jobId") int jobId);

	@Select(DEF_SELECT_JOB_INFO)
	ResultSetSlice<Map<String, Object>> selectJobInfo(@Arg("clusterName") String clusterName);

	@Get(DEF_SELECT_JOB_DETAIL)
	Map<String, Object> selectJobDetail(@Arg("clusterName") String clusterName, @Arg("groupName") String groupName,
			@Arg("jobName") String jobName, @Arg("jobClassName") String jobClassName);

	@Query(DEF_SELECT_JOB_LOG)
	List<Map<String, Object>> selectJobLog(@Arg("jobId") int jobId, @Arg("traceId") long traceId);

	@Query(DEF_SELECT_JOB_EXCEPTION)
	List<Map<String, Object>> selectJobException(@Arg("jobId") int jobId, @Arg("traceId") long traceId);

}
