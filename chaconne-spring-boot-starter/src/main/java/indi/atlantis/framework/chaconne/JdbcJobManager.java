package indi.atlantis.framework.chaconne;

import static com.github.paganini2008.devtools.beans.BeanUtils.convertAsBean;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.jdbc.PageRequest;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

import indi.atlantis.framework.chaconne.model.JobDetail;
import indi.atlantis.framework.chaconne.model.JobKeyQuery;
import indi.atlantis.framework.chaconne.model.JobLog;
import indi.atlantis.framework.chaconne.model.JobRuntimeDetail;
import indi.atlantis.framework.chaconne.model.JobStackTrace;
import indi.atlantis.framework.chaconne.model.JobTrace;
import indi.atlantis.framework.chaconne.model.JobTracePageQuery;
import indi.atlantis.framework.chaconne.model.JobTraceQuery;
import indi.atlantis.framework.chaconne.model.JobTriggerDetail;
import indi.atlantis.framework.chaconne.model.PageQuery;
import indi.atlantis.framework.chaconne.model.TriggerDescription;
import indi.atlantis.framework.chaconne.model.TriggerDescription.Dependency;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JdbcJobManager
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class JdbcJobManager implements JobManager {

	@Autowired
	private LifeCycleListenerContainer lifeCycleListenerContainer;

	@Autowired
	private JobIdCache jobIdCache;

	@Autowired
	private JobDao jobDao;

	@Autowired
	private JobQueryDao jobQueryDao;

	@Override
	public String[] selectClusterNames() throws Exception {
		List<String> clusterNames = jobQueryDao.selectClusterNames();
		return clusterNames.toArray(new String[0]);
	}

	@Override
	public JobState pauseJob(JobKey jobKey) throws Exception {
		if (hasJob(jobKey) && hasJobState(jobKey, JobState.SCHEDULING)) {
			if (log.isTraceEnabled()) {
				log.trace("Pause job: " + jobKey);
			}
			return setJobState(jobKey, JobState.PAUSED);
		}
		return getJobRuntimeDetail(jobKey).getJobState();
	}

	@Override
	public JobState resumeJob(JobKey jobKey) throws Exception {
		if (hasJob(jobKey) && hasJobState(jobKey, JobState.PAUSED)) {
			if (log.isTraceEnabled()) {
				log.trace("Resume job: " + jobKey);
			}
			return setJobState(jobKey, JobState.SCHEDULING);
		}
		return getJobRuntimeDetail(jobKey).getJobState();
	}

	@Override
	public int getJobId(final JobKey jobKey) throws Exception {
		return jobIdCache.getJobId(jobKey, () -> {
			return doGetJobId(jobKey);
		});
	}

	private int doGetJobId(JobKey jobKey) {
		Integer jobId = jobQueryDao.selectJobId(jobKey.getClusterName(), jobKey.getGroupName(), jobKey.getJobName(),
				jobKey.getJobClassName());
		if (jobId == null) {
			throw new JobBeanNotFoundException(jobKey);
		}
		return jobId.intValue();
	}

	@Override
	public int persistJob(JobDefinition jobDef, String attachment) throws Exception {
		final JobKey jobKey = JobKey.of(jobDef);

		int jobId;
		Trigger trigger;
		TriggerType triggerType;
		DependencyType dependencyType = null;
		boolean hasDependentKeys = ArrayUtils.isNotEmpty(jobDef.getDependentKeys());
		boolean hasSubKeys = ArrayUtils.isNotEmpty(jobDef.getSubJobKeys());
		if (hasDependentKeys && hasSubKeys) {
			dependencyType = DependencyType.MIXED;
		} else if (hasDependentKeys && !hasSubKeys) {
			dependencyType = DependencyType.SERIAL;
		} else if (!hasDependentKeys && hasSubKeys) {
			dependencyType = DependencyType.PARALLEL;
		}

		if (hasJob(jobKey)) {
			jobId = getJobId(jobKey);
			Map<String, Object> kwargs = new HashMap<String, Object>();
			kwargs.put("description", jobDef.getDescription());
			kwargs.put("attachment", attachment);
			kwargs.put("email", jobDef.getEmail());
			kwargs.put("retries", jobDef.getRetries());
			kwargs.put("weight", jobDef.getWeight());
			kwargs.put("timeout", jobDef.getTimeout());
			kwargs.put("clusterName", jobDef.getClusterName());
			kwargs.put("groupName", jobDef.getGroupName());
			kwargs.put("jobName", jobDef.getJobName());
			kwargs.put("jobClassName", jobDef.getJobClassName());
			jobDao.updateJobDetail(kwargs);

			trigger = jobDef.getTrigger();
			triggerType = trigger.getTriggerType();

			if (dependencyType != null) {
				TriggerDescription triggerDescription = trigger.getTriggerDescription();
				triggerDescription.setDependency(new Dependency());
				Dependency dependency = triggerDescription.getDependency();
				dependency.setDependencyType(dependencyType);
				switch (dependencyType) {
				case SERIAL:
					dependency.setDependentKeys(jobDef.getDependentKeys());
					dependency.setTriggerType(triggerType);
					triggerDescription.setCron(null);
					triggerDescription.setPeriodic(null);
					break;
				case PARALLEL:
					dependency.setSubJobKeys(jobDef.getSubJobKeys());
					dependency.setCompletionRate(jobDef.getCompletionRate());
					dependency.setTriggerType(triggerType);
					if (triggerType == TriggerType.CRON) {
						triggerDescription.getDependency().setCron(triggerDescription.getCron());
						triggerDescription.setCron(null);
					} else if (triggerType == TriggerType.PERIODIC) {
						triggerDescription.getDependency().setPeriodic(triggerDescription.getPeriodic());
						triggerDescription.setPeriodic(null);
					}
					break;
				case MIXED:
					dependency.setDependentKeys(jobDef.getDependentKeys());
					dependency.setSubJobKeys(jobDef.getSubJobKeys());
					dependency.setTriggerType(triggerType);
					triggerDescription.setCron(null);
					triggerDescription.setPeriodic(null);
					break;
				}

			}
			kwargs.put("triggerType", hasDependentKeys || hasSubKeys ? TriggerType.DEPENDENT.getValue() : triggerType.getValue());
			kwargs.put("triggerDescription", JacksonUtils.toJsonString(trigger.getTriggerDescription()));
			kwargs.put("startDate", trigger.getStartDate() != null ? new Timestamp(trigger.getStartDate().getTime()) : null);
			kwargs.put("endDate", trigger.getEndDate() != null ? new Timestamp(trigger.getEndDate().getTime()) : null);
			kwargs.put("repeatCount", trigger.getRepeatCount());
			kwargs.put("jobId", jobId);
			jobDao.updateJobTrigger(kwargs);
			log.info("Merge job '{}' ok.", jobKey);

			if (dependencyType != null) {
				switch (dependencyType) {
				case SERIAL:
					handleJobDependency(jobKey, jobId, jobDef.getDependentKeys(), DependencyType.SERIAL);
					break;
				case PARALLEL:
					handleJobDependency(jobKey, jobId, jobDef.getSubJobKeys(), DependencyType.PARALLEL);
					break;
				case MIXED:
					handleJobDependency(jobKey, jobId, jobDef.getDependentKeys(), DependencyType.SERIAL);
					handleJobDependency(jobKey, jobId, jobDef.getSubJobKeys(), DependencyType.PARALLEL);
					break;
				}

			}

			lifeCycleListenerContainer.onChange(jobKey, JobLifeCycle.REFRESH);
			return jobId;
		} else {

			Map<String, Object> kwargs = new HashMap<String, Object>();
			kwargs.put("clusterName", jobDef.getClusterName());
			kwargs.put("groupName", jobDef.getGroupName());
			kwargs.put("jobName", jobDef.getJobName());
			kwargs.put("jobClassName", jobDef.getJobClassName());
			kwargs.put("description", jobDef.getDescription());
			kwargs.put("attachment", attachment);
			kwargs.put("email", jobDef.getEmail());
			kwargs.put("retries", jobDef.getRetries());
			kwargs.put("weight", jobDef.getWeight());
			kwargs.put("timeout", jobDef.getTimeout());
			kwargs.put("createDate", new Timestamp(System.currentTimeMillis()));
			jobId = jobDao.saveJobDetail(kwargs);

			kwargs = new HashMap<String, Object>();
			kwargs.put("jobId", jobId);
			kwargs.put("jobState", JobState.NOT_SCHEDULED.getValue());
			jobDao.saveJobRuntimeDetail(kwargs);

			trigger = jobDef.getTrigger();
			triggerType = trigger.getTriggerType();

			if (dependencyType != null) {
				TriggerDescription triggerDescription = trigger.getTriggerDescription();
				triggerDescription.setDependency(new Dependency());
				Dependency dependency = triggerDescription.getDependency();
				dependency.setDependencyType(dependencyType);
				switch (dependencyType) {
				case SERIAL:
					dependency.setDependentKeys(jobDef.getDependentKeys());
					triggerDescription.setCron(null);
					triggerDescription.setPeriodic(null);
					break;
				case PARALLEL:
					dependency.setSubJobKeys(jobDef.getSubJobKeys());
					dependency.setCompletionRate(jobDef.getCompletionRate());
					dependency.setTriggerType(triggerType);
					if (triggerType == TriggerType.CRON) {
						triggerDescription.getDependency().setCron(triggerDescription.getCron());
						triggerDescription.setCron(null);
					} else if (triggerType == TriggerType.PERIODIC) {
						triggerDescription.getDependency().setPeriodic(triggerDescription.getPeriodic());
						triggerDescription.setPeriodic(null);
					}
					break;
				case MIXED:
					dependency.setDependentKeys(jobDef.getDependentKeys());
					dependency.setSubJobKeys(jobDef.getSubJobKeys());
					triggerDescription.setCron(null);
					triggerDescription.setPeriodic(null);
					break;
				}

			}

			kwargs = new HashMap<String, Object>();
			kwargs.put("jobId", jobId);
			kwargs.put("triggerType", hasDependentKeys || hasSubKeys ? TriggerType.DEPENDENT.getValue() : triggerType.getValue());
			kwargs.put("triggerDescription", JacksonUtils.toJsonString(trigger.getTriggerDescription()));
			kwargs.put("startDate", trigger.getStartDate() != null ? new Timestamp(trigger.getStartDate().getTime()) : null);
			kwargs.put("endDate", trigger.getEndDate() != null ? new Timestamp(trigger.getEndDate().getTime()) : null);
			kwargs.put("repeatCount", trigger.getRepeatCount());
			jobDao.saveJobTriggerDetail(kwargs);
			log.info("Add job '{}' ok.", jobKey);

			if (dependencyType != null) {
				switch (dependencyType) {
				case SERIAL:
					handleJobDependency(jobKey, jobId, jobDef.getDependentKeys(), DependencyType.SERIAL);
					break;
				case PARALLEL:
					handleJobDependency(jobKey, jobId, jobDef.getSubJobKeys(), DependencyType.PARALLEL);
					break;
				case MIXED:
					handleJobDependency(jobKey, jobId, jobDef.getDependentKeys(), DependencyType.SERIAL);
					handleJobDependency(jobKey, jobId, jobDef.getSubJobKeys(), DependencyType.PARALLEL);
					break;
				}

			}

			lifeCycleListenerContainer.onChange(jobKey, JobLifeCycle.CREATION);
			return jobId;
		}
	}

	/**
	 * Save Job Dependency Info
	 * 
	 * @param jobKey
	 * @param jobId
	 * @param dependencies
	 * @param dependencyType
	 * @throws SQLException
	 */
	private void handleJobDependency(JobKey jobKey, int jobId, JobKey[] dependencies, DependencyType dependencyType) throws Exception {
		List<Integer> dependentIds = new ArrayList<Integer>();
		if (ArrayUtils.isNotEmpty(dependencies)) {
			for (JobKey dependency : dependencies) {
				if (hasJob(dependency)) {
					dependentIds.add(getJobId(dependency));
				}
			}
		}
		if (dependentIds.size() > 0) {
			Map<String, Object> kwargs = new HashMap<String, Object>();
			kwargs.put("jobId", jobId);
			kwargs.put("dependencyType", dependencyType.getValue());
			jobDao.deleteJobDependency(kwargs);

			for (Integer dependentId : dependentIds) {
				kwargs = new HashMap<String, Object>();
				kwargs.put("jobId", jobId);
				kwargs.put("dependentJobId", dependentId);
				kwargs.put("dependencyType", dependencyType.getValue());
				jobDao.saveJobDependency(kwargs);
			}
			log.info("Add job dependency by key '{}' ok.", jobKey);

		}
	}

	@Override
	public JobState finishJob(JobKey jobKey) throws Exception {
		if (!hasJob(jobKey)) {
			throw new JobBeanNotFoundException(jobKey);
		}
		if (!hasJobState(jobKey, JobState.NOT_SCHEDULED)) {
			throw new JobException("Please unschedule the job before you delete it.");
		}
		try {
			lifeCycleListenerContainer.onChange(jobKey, JobLifeCycle.DELETION);
			return setJobState(jobKey, JobState.FINISHED);
		} finally {
			jobIdCache.evict(jobKey);
		}
	}

	@Override
	public boolean hasJob(JobKey jobKey) {
		Integer result = jobQueryDao.selectJobExists(jobKey.getClusterName(), jobKey.getGroupName(), jobKey.getJobName(),
				jobKey.getJobClassName());
		return result != null && result.intValue() > 0;
	}

	@Override
	public JobState setJobState(JobKey jobKey, JobState jobState) throws Exception {
		final int jobId = getJobId(jobKey);
		Map<String, Object> kwargs = new HashMap<String, Object>();
		kwargs.put("jobState", jobState.getValue());
		kwargs.put("jobId", jobId);
		jobDao.updateJobState(kwargs);
		return getJobRuntimeDetail(jobKey).getJobState();
	}

	@Override
	public boolean hasJobState(JobKey jobKey, JobState jobState) throws Exception {
		JobRuntimeDetail jobRuntime = getJobRuntimeDetail(jobKey);
		return jobRuntime.getJobState() == jobState;
	}

	@Override
	public JobDetail getJobDetail(JobKey jobKey, boolean detailed) throws Exception {
		JobDetail jobDetail = doGetJobDetail(jobKey);
		if (detailed) {
			jobDetail.setJobRuntime(getJobRuntimeDetail(jobKey));
			jobDetail.setJobTriggerDetail(getJobTriggerDetail(jobKey));
		}
		return jobDetail;
	}

	private JobDetail doGetJobDetail(JobKey jobKey) throws SQLException {
		Map<String, Object> data = jobQueryDao.selectJobDetail(jobKey.getClusterName(), jobKey.getGroupName(), jobKey.getJobName(),
				jobKey.getJobClassName());
		if (data == null) {
			throw new JobBeanNotFoundException(jobKey);
		}
		JobDetail jobDetail = convertAsBean(data, JobDetail.class);
		jobDetail.setJobKey(JobKey.of(data));
		return jobDetail;
	}

	@Override
	public JobTriggerDetail getJobTriggerDetail(JobKey jobKey) throws Exception {
		final int jobId = getJobId(jobKey);
		Map<String, Object> data = jobQueryDao.selectJobTriggerDetail(jobId);
		if (data == null) {
			throw new JobBeanNotFoundException(jobKey);
		}
		return convertAsBean(data, JobTriggerDetail.class);
	}

	@Override
	public boolean hasRelations(JobKey jobKey, DependencyType dependencyType) throws Exception {
		int jobId = getJobId(jobKey);
		Integer rowCount = jobQueryDao.selectJobHasRelations(jobId, dependencyType.getValue());
		return rowCount != null && rowCount.intValue() > 0;
	}

	@Override
	public JobKey[] getRelations(JobKey jobKey, DependencyType dependencyType) throws Exception {
		Set<JobKey> jobKeys = new TreeSet<JobKey>();
		int jobId = getJobId(jobKey);
		List<Map<String, Object>> dataList = jobQueryDao.selectJobRelations(jobId, dependencyType.getValue());
		for (Map<String, Object> data : dataList) {
			jobKeys.add(JobKey.of(data));
		}
		return jobKeys.toArray(new JobKey[0]);
	}

	@Override
	public JobKey[] getDependentKeys(JobKey jobKey, DependencyType dependencyType) throws Exception {
		int jobId = getJobId(jobKey);
		Set<JobKey> jobKeys = new TreeSet<JobKey>();
		List<Map<String, Object>> dataList = jobQueryDao.selectDependentJobDetail(jobId, dependencyType.getValue());
		for (Map<String, Object> data : dataList) {
			jobKeys.add(JobKey.of(data));
		}
		return jobKeys.toArray(new JobKey[0]);
	}

	@Override
	public JobRuntimeDetail getJobRuntimeDetail(JobKey jobKey) throws Exception {
		int jobId = getJobId(jobKey);
		Map<String, Object> data = jobQueryDao.selectJobRuntime(jobId);
		if (data == null) {
			throw new JobBeanNotFoundException(jobKey);
		}
		return convertAsBean(data, JobRuntimeDetail.class);
	}

	@Override
	public JobKey[] getJobKeys(JobKeyQuery jobQuery) throws SQLException {
		Set<JobKey> jobKeys = new TreeSet<JobKey>();
		Map<String, Object> kwargs = new HashMap<>();
		StringBuilder sql = new StringBuilder();
		if (StringUtils.isNotBlank(jobQuery.getClusterName())) {
			sql.append(" and a.cluster_name=:clusterName");
			kwargs.put("clusterName", jobQuery.getClusterName());
		} else if (StringUtils.isNotBlank(jobQuery.getClusterNames())) {
			sql.append(" and a.cluster_name in (:clusterNames)");
			kwargs.put("clusterNames", jobQuery.getClusterNames());
		}
		if (StringUtils.isNotBlank(jobQuery.getGroupName())) {
			sql.append(" and a.group_name=:groupName");
			kwargs.put("groupName", jobQuery.getGroupName());
		} else if (StringUtils.isNotBlank(jobQuery.getGroupNames())) {
			sql.append(" and a.group_name in (:groupNames)");
			kwargs.put("groupNames", jobQuery.getGroupNames());
		}
		List<Map<String, Object>> dataList = jobQueryDao.selectJobKeysByTriggerType(sql.toString(), kwargs,
				jobQuery.getTriggerType().getValue());
		if (CollectionUtils.isNotEmpty(dataList)) {
			for (Map<String, Object> data : dataList) {
				jobKeys.add(JobKey.of(data));
			}
		}
		return jobKeys.toArray(new JobKey[0]);
	}

	@Override
	public void selectJobDetail(PageQuery<JobDetail> pageQuery) {
		final ResultSetSlice<Map<String, Object>> delegate = jobQueryDao.selectJobInfo(pageQuery.getClusterName());
		ResultSetSlice<JobDetail> resultSetSlice = new ResultSetSlice<JobDetail>() {

			@Override
			public int rowCount() {
				return delegate.rowCount();
			}

			@Override
			public List<JobDetail> list(int maxResults, int firstResult) {
				List<JobDetail> dataList = new ArrayList<JobDetail>(maxResults);
				for (Map<String, Object> data : delegate.list(maxResults, firstResult)) {
					JobDetail jobDetail = convertAsBean(data, JobDetail.class);
					JobKey jobKey = convertAsBean(data, JobKey.class);
					JobRuntimeDetail jobRuntime = convertAsBean(data, JobRuntimeDetail.class);
					JobTriggerDetail jobTriggerDetail = convertAsBean(data, JobTriggerDetail.class);
					jobDetail.setJobKey(jobKey);
					jobDetail.setJobRuntime(jobRuntime);
					jobDetail.setJobTriggerDetail(jobTriggerDetail);
					dataList.add(jobDetail);
				}
				return dataList;
			}

		};

		PageResponse<JobDetail> pageResponse = resultSetSlice.list(PageRequest.of(pageQuery.getPage(), pageQuery.getSize()));
		int rows = pageResponse.getTotalRecords();
		pageQuery.setRows(rows);
		pageQuery.setContent(pageResponse.getContent());
		pageQuery.setNextPage(pageResponse.hasNextPage());
	}

	@Override
	public void selectJobTrace(JobTracePageQuery<JobTrace> pageQuery) throws Exception {
		Date startDate = pageQuery.getStartDate();
		if (startDate == null) {
			startDate = DateUtils.addDays(new Date(), -30);
			startDate = DateUtils.setTime(startDate, 0, 0, 0);
		}
		Date endDate = pageQuery.getEndDate();
		if (endDate == null) {
			endDate = DateUtils.setTime(new Date(), 23, 59, 59);
		}
		int jobId = getJobId(pageQuery.getJobKey());
		final ResultSetSlice<Map<String, Object>> delegate = jobQueryDao.selectJobTrace(jobId, startDate, endDate);
		ResultSetSlice<JobTrace> resultSetSlice = new ResultSetSlice<JobTrace>() {

			@Override
			public int rowCount() {
				return delegate.rowCount();
			}

			@Override
			public List<JobTrace> list(int maxResults, int firstResult) {
				List<JobTrace> dataList = new ArrayList<JobTrace>(maxResults);
				for (Map<String, Object> data : delegate.list(maxResults, firstResult)) {
					JobTrace jobTrace = convertAsBean(data, JobTrace.class);
					dataList.add(jobTrace);
				}
				return dataList;
			}

		};
		PageResponse<JobTrace> pageResponse = resultSetSlice.list(PageRequest.of(pageQuery.getPage(), pageQuery.getSize()));
		int rows = pageResponse.getTotalRecords();
		pageQuery.setRows(rows);
		pageQuery.setContent(pageResponse.getContent());
		pageQuery.setNextPage(pageResponse.hasNextPage());
	}

	@Override
	public JobStackTrace[] selectJobStackTrace(JobTraceQuery query) throws Exception {
		List<JobStackTrace> results = new ArrayList<JobStackTrace>();
		int jobId = getJobId(query.getJobKey());
		List<Map<String, Object>> dataList = jobQueryDao.selectJobException(jobId, query.getTraceId());
		for (Map<String, Object> data : dataList) {
			results.add(convertAsBean(data, JobStackTrace.class));
		}
		return results.toArray(new JobStackTrace[0]);
	}

	@Override
	public JobLog[] selectJobLog(JobTraceQuery query) throws Exception {
		List<JobLog> results = new ArrayList<JobLog>();
		int jobId = getJobId(query.getJobKey());
		List<Map<String, Object>> dataList = jobQueryDao.selectJobLog(jobId, query.getTraceId());
		for (Map<String, Object> data : dataList) {
			results.add(convertAsBean(data, JobLog.class));
		}
		return results.toArray(new JobLog[0]);
	}

}
