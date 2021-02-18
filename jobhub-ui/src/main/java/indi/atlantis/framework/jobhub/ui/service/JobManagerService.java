package indi.atlantis.framework.jobhub.ui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.ArrayUtils;

import indi.atlantis.framework.jobhub.JobKey;
import indi.atlantis.framework.jobhub.JobManager;
import indi.atlantis.framework.jobhub.model.JobDetail;
import indi.atlantis.framework.jobhub.model.JobLog;
import indi.atlantis.framework.jobhub.model.JobPersistParam;
import indi.atlantis.framework.jobhub.model.JobStackTrace;
import indi.atlantis.framework.jobhub.model.JobTrace;
import indi.atlantis.framework.jobhub.model.JobTracePageQuery;
import indi.atlantis.framework.jobhub.model.JobTraceQuery;
import indi.atlantis.framework.jobhub.model.PageQuery;
import indi.atlantis.framework.jobhub.ui.JobLogForm;
import indi.atlantis.framework.jobhub.ui.JobTraceForm;

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
