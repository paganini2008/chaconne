/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package indi.atlantis.framework.chaconne.utils;

import indi.atlantis.framework.chaconne.DependencyType;
import indi.atlantis.framework.chaconne.JobDefinition;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.Trigger;
import indi.atlantis.framework.chaconne.model.JobPersistParameter;
import indi.atlantis.framework.chaconne.model.JobTriggerParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * GenericJobDefinition
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class GenericJobDefinition implements JobDefinition {

	// Generial settings
	private final JobKey jobKey;
	private final String description;
	private final String email;
	private final int retries;
	private final int weight;
	private final long timeout;

	// Dependency settings
	private final JobKey[] dependentKeys;
	private final JobKey[] forkKeys;
	private final float completionRate;

	// Trigger settings
	private final Trigger trigger;

	GenericJobDefinition(Builder builder) {
		this.jobKey = builder.jobKey;
		this.description = builder.description;
		this.email = builder.email;
		this.retries = builder.retries;
		this.weight = builder.weight;
		this.timeout = builder.timeout;
		this.trigger = builder.trigger;
		this.dependentKeys = builder.dependentKeys;
		this.forkKeys = builder.forkKeys;
		this.completionRate = builder.completionRate;
	}

	@Override
	public String getClusterName() {
		return jobKey.getClusterName();
	}

	@Override
	public String getGroupName() {
		return jobKey.getGroupName();
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
	public Trigger getTrigger() {
		return trigger;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getRetries() {
		return retries;
	}

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public long getTimeout() {
		return timeout;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public JobKey[] getDependentKeys() {
		return dependentKeys;
	}

	@Override
	public JobKey[] getForkKeys() {
		return forkKeys;
	}

	@Override
	public float getCompletionRate() {
		return completionRate;
	}

	@Accessors(chain = true)
	@Getter
	@Setter
	public static class Builder {

		private final JobKey jobKey;
		private String description = "";
		private String email = "";
		private int retries;
		private int weight = 100;
		private long timeout = -1L;

		private DependencyType dependencyType = DependencyType.SERIAL;
		private JobKey[] dependentKeys;
		private JobKey[] forkKeys;
		private float completionRate = -1F;

		private Trigger trigger;

		Builder(JobKey jobKey) {
			this.jobKey = jobKey;
		}

		Builder(String clusterName, String groupName, String jobName, String jobClassName) {
			this.jobKey = JobKey.by(clusterName, groupName, jobName, jobClassName);
		}

		public GenericJobDefinition build() {
			return new GenericJobDefinition(this);
		}
	}

	public JobPersistParameter toParameter() {
		JobPersistParameter parameter = new JobPersistParameter();
		parameter.setDescription(description);
		parameter.setEmail(email);
		parameter.setRetries(retries);
		parameter.setTimeout(timeout);
		parameter.setWeight(weight);
		parameter.setJobKey(jobKey);
		parameter.setDependentKeys(dependentKeys);
		parameter.setForkKeys(forkKeys);
		parameter.setCompletionRate(completionRate);

		JobTriggerParameter triggerParam = new JobTriggerParameter();
		if (trigger != null) {
			triggerParam.setTriggerType(trigger.getTriggerType());
			triggerParam.setTriggerDescription(trigger.getTriggerDescription());
			triggerParam.setStartDate(trigger.getStartDate());
			triggerParam.setEndDate(trigger.getEndDate());
			triggerParam.setRepeatCount(trigger.getRepeatCount());
		}
		parameter.setTrigger(triggerParam);
		return parameter;
	}

	public static Builder parse(JobPersistParameter parameter) {
		Builder builder = newJob(parameter.getJobKey()).setDescription(parameter.getDescription()).setEmail(parameter.getEmail())
				.setRetries(parameter.getRetries()).setTimeout(parameter.getTimeout()).setWeight(parameter.getWeight())
				.setDependentKeys(parameter.getDependentKeys()).setForkKeys(parameter.getForkKeys());
		GenericTrigger.Builder triggerBuilder = GenericTrigger.parse(parameter.getTrigger());
		builder.setTrigger(triggerBuilder.build());
		return builder;
	}

	public static Builder newJob(JobKey jobKey) {
		return new Builder(jobKey);
	}

	public static Builder newJob(String clusterName, String groupName, String jobName, String jobClassName) {
		return new Builder(clusterName, groupName, jobName, jobClassName);
	}

	public static Builder newJob(String clusterName, String groupName, String jobName, Class<?> jobClass) {
		return new Builder(clusterName, groupName, jobName, jobClass.getName());
	}

}
