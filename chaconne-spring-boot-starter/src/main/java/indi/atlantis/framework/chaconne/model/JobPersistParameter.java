/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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
package indi.atlantis.framework.chaconne.model;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.model.TriggerDescription.Dependency;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * JobPersistParameter
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
@ToString
public class JobPersistParameter {

	private JobKey jobKey;
	private String description;
	private String email;
	private int retries;
	private long timeout = -1L;
	private int weight = 100;
	private JobKey[] dependentKeys;
	private JobKey[] forkKeys;
	private float completionRate = -1F;

	private JobTriggerParameter trigger;
	private String attachment;

	public JobPersistParameter() {
	}

	public JobPersistParameter(String clusterName, String groupName, String jobName, String jobClassName) {
		this.jobKey = JobKey.by(clusterName, groupName, jobName, jobClassName);
	}

	public JobPersistParameter(JobKey jobKey) {
		this.jobKey = jobKey;
	}

	public static JobPersistParameter forExample() {
		JobPersistParameter parameter = new JobPersistParameter();
		parameter.setJobKey(JobKey.by("yourCluster", "yourGroup", "yourJob", "com.yourcompany.yourapp.YourJob"));
		parameter.setDescription("Describe your job shortly");
		parameter.setEmail("Set your email if job run abnormally");
		parameter.setRetries(0);
		parameter.setTimeout(-1L);
		parameter.setAttachment("Set initial parameter of your job. Data with json format is recommended.");
		parameter.setTrigger(new JobTriggerParameter("*/5 * * * * ?"));
		return parameter;
	}

	public static JobPersistParameter wrap(JobDetail jobDetail) {
		JobPersistParameter parameter = new JobPersistParameter(jobDetail.getJobKey());
		parameter.setAttachment(jobDetail.getAttachment());
		parameter.setDescription(jobDetail.getDescription());
		parameter.setEmail(jobDetail.getEmail());
		parameter.setRetries(jobDetail.getRetries());
		parameter.setTimeout(jobDetail.getTimeout());
		parameter.setWeight(jobDetail.getWeight());

		JobTriggerDetail triggerDetail = jobDetail.getJobTriggerDetail();
		TriggerDescription triggerDescription = triggerDetail.getTriggerDescriptionObject();
		Dependency dependency = triggerDescription.getDependency();
		if (dependency != null) {
			parameter.setCompletionRate(dependency.getCompletionRate());
			parameter.setDependentKeys(dependency.getDependentKeys());
			parameter.setForkKeys(dependency.getForkKeys());
			triggerDescription.setCron(dependency.getCron());
			triggerDescription.setPeriodic(dependency.getPeriodic());
			triggerDescription.setDependency(null);
		}

		JobTriggerParameter trigger = new JobTriggerParameter();
		trigger.setTriggerType(triggerDetail.getTriggerType());
		trigger.setStartDate(triggerDetail.getStartDate());
		trigger.setEndDate(triggerDetail.getEndDate());
		trigger.setRepeatCount(triggerDetail.getRepeatCount());
		trigger.setTriggerDescription(triggerDescription);
		parameter.setTrigger(trigger);

		return parameter;
	}

}
