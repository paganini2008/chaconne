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

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import io.atlantisframework.chaconne.Job;
import io.atlantisframework.chaconne.JobAdmin;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.JobState;
import io.atlantisframework.chaconne.Trigger;
import io.atlantisframework.chaconne.model.JobTriggerDetail;
import io.atlantisframework.chaconne.utils.GenericTrigger;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DetachedModeJobBeanProxy
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class DetachedModeJobBeanProxy implements Job {

	private final JobKey jobKey;
	private final JobTriggerDetail triggerDetail;

	@Autowired
	private JobAdmin jobAdmin;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobServerRegistry jobServerRegistry;

	public DetachedModeJobBeanProxy(JobKey jobKey, JobTriggerDetail triggerDetail) {
		this.jobKey = jobKey;
		this.triggerDetail = triggerDetail;
	}

	@Override
	public String getClusterName() {
		return jobKey.getClusterName();
	}

	@Override
	public String getJobName() {
		return jobKey.getJobName();
	}

	@Override
	public String getJobClassName() {
		return jobKey.getJobClassName();
	}

	@Override
	public String getGroupName() {
		return jobKey.getGroupName();
	}

	@Override
	public Trigger getTrigger() {
		return GenericTrigger.Builder.newTrigger().setTriggerType(triggerDetail.getTriggerType())
				.setTriggerDescription(triggerDetail.getTriggerDescriptionObject()).setStartDate(triggerDetail.getStartDate())
				.setEndDate(triggerDetail.getEndDate()).setRepeatCount(triggerDetail.getRepeatCount()).build();
	}

	@Override
	public Object execute(JobKey jobKey, Object result, Logger log) {
		try {
			return jobAdmin.triggerJob(jobKey, result);
		} catch (JobServiceAccessException e) {
			if (log.isWarnEnabled()) {
				log.warn(e.getMessage());
			}
			resetJobState();
			for (String contextPath : e.getContextPaths()) {
				jobServerRegistry.unregisterJobExecutor(jobKey.getClusterName(), jobKey.getGroupName(), contextPath);
			}
		} catch (UnavailableJobServiceException e) {
			if (log.isWarnEnabled()) {
				log.warn("Job: " + jobKey.toString() + " has no available resource to execute now.");
			}
			resetJobState();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return JobState.NONE;
	}

	private void resetJobState() {
		try {
			jobManager.setJobState(jobKey, JobState.SCHEDULING);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		log.error(e.getMessage(), e);
	}

}
