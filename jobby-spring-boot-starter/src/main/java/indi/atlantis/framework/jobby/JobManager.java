package indi.atlantis.framework.jobby;

import indi.atlantis.framework.jobby.model.JobDetail;
import indi.atlantis.framework.jobby.model.JobKeyQuery;
import indi.atlantis.framework.jobby.model.JobLog;
import indi.atlantis.framework.jobby.model.JobRuntime;
import indi.atlantis.framework.jobby.model.JobStackTrace;
import indi.atlantis.framework.jobby.model.JobTrace;
import indi.atlantis.framework.jobby.model.JobTracePageQuery;
import indi.atlantis.framework.jobby.model.JobTraceQuery;
import indi.atlantis.framework.jobby.model.JobTriggerDetail;
import indi.atlantis.framework.jobby.model.PageQuery;

/**
 * 
 * JobManager
 *
 * @author Jimmy Hoff
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

	JobRuntime getJobRuntime(JobKey jobKey) throws Exception;

	void selectJobDetail(PageQuery<JobDetail> pageQuery) throws Exception;

	void selectJobTrace(JobTracePageQuery<JobTrace> pageQuery) throws Exception;

	JobStackTrace[] selectJobStackTrace(JobTraceQuery query) throws Exception;

	JobLog[] selectJobLog(JobTraceQuery query) throws Exception;

}
