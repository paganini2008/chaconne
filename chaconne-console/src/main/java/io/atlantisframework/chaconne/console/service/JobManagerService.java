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
package io.atlantisframework.chaconne.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.StringUtils;

import io.atlantisframework.chaconne.JacksonUtils;
import io.atlantisframework.chaconne.JobAdmin;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.JobState;
import io.atlantisframework.chaconne.console.utils.JobLogForm;
import io.atlantisframework.chaconne.console.utils.JobTraceForm;
import io.atlantisframework.chaconne.model.JobDetail;
import io.atlantisframework.chaconne.model.JobLog;
import io.atlantisframework.chaconne.model.JobPersistParameter;
import io.atlantisframework.chaconne.model.JobStackTrace;
import io.atlantisframework.chaconne.model.JobTrace;
import io.atlantisframework.chaconne.model.JobTracePageQuery;
import io.atlantisframework.chaconne.model.JobTraceQuery;
import io.atlantisframework.chaconne.model.PageQuery;

/**
 * 
 * JobManagerService
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Component
public class JobManagerService {

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobAdmin jobAdmin;

	public int saveJob(JobPersistParameter param) throws Exception {
		return jobManager.persistJob(param);
	}

	public JobState deleteJob(String identifier) throws Exception {
		final JobKey jobKey = JobKey.decode(identifier);
		JobState jobState = jobAdmin.unscheduleJob(jobKey);
		if (jobState == JobState.NOT_SCHEDULED) {
			jobState = jobManager.finishJob(jobKey);
		}
		return jobState;
	}

	public JobState toggleJob(String identifier) throws Exception {
		final JobKey jobKey = JobKey.decode(identifier);
		if (jobManager.hasJobState(jobKey, JobState.PAUSED)) {
			return jobManager.resumeJob(jobKey);
		}
		return jobManager.pauseJob(jobKey);
	}

	public void triggerJob(String identifier, String attachment) throws Exception {
		final JobKey jobKey = JobKey.decode(identifier);
		if (StringUtils.isBlank(attachment)) {
			JobDetail jobDetail = jobManager.getJobDetail(jobKey, false);
			attachment = jobDetail.getAttachment();
		}
		jobAdmin.triggerJob(jobKey, attachment);
	}

	public String[] selectRegisteredClusterNames() throws Exception {
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
		JobDetail jobDetail = jobManager.getJobDetail(JobKey.decode(jobKey), true);
		String formattedTriggerDescription = JacksonUtils.toJsonString(jobDetail.getJobTriggerDetail().getTriggerDescriptionObject(), true);
		jobDetail.getJobTriggerDetail().setTriggerDescription(formattedTriggerDescription);
		return jobDetail;
	}

	public JobLog[] selectJobLog(JobLogForm form) throws Exception {
		return jobManager.selectJobLog(new JobTraceQuery(JobKey.decode(form.getJobKey()), form.getTraceId()));
	}

	public JobStackTrace[] selectJobStackTrace(JobLogForm form) throws Exception {
		return jobManager.selectJobStackTrace(new JobTraceQuery(JobKey.decode(form.getJobKey()), form.getTraceId()));
	}

}
