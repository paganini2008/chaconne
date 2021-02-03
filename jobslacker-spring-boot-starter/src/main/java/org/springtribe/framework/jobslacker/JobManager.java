package org.springtribe.framework.jobslacker;

import org.springtribe.framework.jobslacker.model.JobDetail;
import org.springtribe.framework.jobslacker.model.JobKeyQuery;
import org.springtribe.framework.jobslacker.model.JobLog;
import org.springtribe.framework.jobslacker.model.JobRuntime;
import org.springtribe.framework.jobslacker.model.JobStackTrace;
import org.springtribe.framework.jobslacker.model.JobTrace;
import org.springtribe.framework.jobslacker.model.JobTracePageQuery;
import org.springtribe.framework.jobslacker.model.JobTraceQuery;
import org.springtribe.framework.jobslacker.model.JobTriggerDetail;
import org.springtribe.framework.jobslacker.model.PageQuery;

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
