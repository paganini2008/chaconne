package org.springtribe.framework.jobslacker.ui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.JobManager;
import org.springtribe.framework.jobslacker.model.JobDetail;
import org.springtribe.framework.jobslacker.model.JobLog;
import org.springtribe.framework.jobslacker.model.JobPersistParam;
import org.springtribe.framework.jobslacker.model.JobStackTrace;
import org.springtribe.framework.jobslacker.model.JobTrace;
import org.springtribe.framework.jobslacker.model.JobTracePageQuery;
import org.springtribe.framework.jobslacker.model.JobTraceQuery;
import org.springtribe.framework.jobslacker.model.PageQuery;
import org.springtribe.framework.jobslacker.ui.JobLogForm;
import org.springtribe.framework.jobslacker.ui.JobTraceForm;

import com.github.paganini2008.devtools.ArrayUtils;

/**
 * 
 * JobManagerService
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Component
public class JobManagerService {

	@Autowired
	private JobManager jobManager;

	public void saveJob(JobPersistParam param) throws Exception {
		jobManager.persistJob(param);
	}

	public String[] selectClusterNames() throws Exception {
		String[] clusterNames = jobManager.selectClusterNames();
		if (ArrayUtils.isEmpty(clusterNames)) {
			clusterNames = new String[] { "mycluster" };
		}
		return clusterNames;
	}

	public PageQuery<JobDetail> selectJobDetail(String clusterName, int page, int size) throws Exception {
		PageQuery<JobDetail> pageQuery = new PageQuery<JobDetail>(page, size);
		pageQuery.setClusterName(clusterName);
		jobManager.selectJobDetail(pageQuery);
		return pageQuery;
	}

	public PageQuery<JobTrace> selectJobTrace(JobTraceForm form, int page, int size) throws Exception {
		JobTracePageQuery<JobTrace> pageQuery = new JobTracePageQuery<JobTrace>(JobKey.decode(form.getJobKey()), page, size);
		pageQuery.setStartDate(form.getStartDate());
		pageQuery.setEndDate(form.getEndDate());
		jobManager.selectJobTrace(pageQuery);
		return pageQuery;
	}

	public JobDetail getJobDetail(String jobKey) throws Exception {
		return jobManager.getJobDetail(JobKey.decode(jobKey), true);
	}

	public JobLog[] selectJobLog(JobLogForm form) throws Exception {
		return jobManager.selectJobLog(new JobTraceQuery(JobKey.decode(form.getJobKey()), form.getTraceId()));
	}

	public JobStackTrace[] selectJobStackTrace(JobLogForm form) throws Exception {
		return jobManager.selectJobStackTrace(new JobTraceQuery(JobKey.decode(form.getJobKey()), form.getTraceId()));
	}

}
