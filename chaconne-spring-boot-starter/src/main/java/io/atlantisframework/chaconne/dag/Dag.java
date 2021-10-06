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
package io.atlantisframework.chaconne.dag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.atlantisframework.chaconne.JobDefinition;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.Trigger;
import io.atlantisframework.chaconne.utils.GenericJobDefinition;

/**
 * 
 * Dag
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class Dag implements DagDefination {

	public Dag(String clusterName, String groupName, String name) {
		this.builder = GenericJobDefinition.newJob(clusterName, groupName, name, StartDagJob.class);
	}

	private DagFlow startNode;
	private GenericJobDefinition.Builder builder;

	public Dag setDescription(String description) {
		builder.setDescription(description);
		return this;
	}

	public Dag setRetries(int retries) {
		builder.setRetries(retries);
		return this;
	}

	public Dag setWeight(int weight) {
		builder.setWeight(weight);
		return this;
	}

	public Dag setTimeout(long timeout) {
		builder.setTimeout(timeout);
		return this;
	}

	public Dag setEmail(String email) {
		builder.setEmail(email);
		return this;
	}

	public Dag setTrigger(Trigger trigger) {
		builder.setTrigger(trigger);
		return this;
	}

	public DagFlow startWith(String clusterName, String groupName, String jobName, String jobClassName) {
		return (startNode = new DagJob(clusterName, groupName, jobName, jobClassName, this));
	}

	@Override
	public JobKey getJobKey() {
		return builder.getJobKey();
	}

	@Override
	public JobDefinition[] getJobDefinitions() {
		List<JobDefinition> jobDefinitions = new ArrayList<JobDefinition>();
		jobDefinitions.add(builder.build());
		jobDefinitions.addAll(Arrays.asList(startNode.getJobDefinitions()));
		return jobDefinitions.toArray(new JobDefinition[0]);
	}

}
