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
package indi.atlantis.framework.chaconne.dag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import indi.atlantis.framework.chaconne.JobDefinition;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.utils.GenericJobDefinition;

/**
 * 
 * DagJob
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class DagJob implements DagFlow {

	DagJob(String clusterName, String groupName, String jobName, String jobClassName, DagDefination previous) {
		this.builder = GenericJobDefinition.newJob(clusterName, groupName, jobName, jobClassName);
		this.previous = previous;
	}

	private final GenericJobDefinition.Builder builder;
	private final DagDefination previous;
	private final List<DagJob> forkDags = new ArrayList<DagJob>();
	private final List<DagDefination> nextDags = new ArrayList<DagDefination>();

	public DagNode setDescription(String description) {
		builder.setDescription(description);
		return this;
	}

	public DagNode setRetries(int retries) {
		builder.setRetries(retries);
		return this;
	}

	public DagNode setWeight(int weight) {
		builder.setWeight(weight);
		return this;
	}

	public DagNode setTimeout(long timeout) {
		builder.setTimeout(timeout);
		return this;
	}

	public DagDefination getPrevious() {
		return previous;
	}

	public DagDefination[] getNext() {
		return nextDags.toArray(new DagDefination[0]);
	}

	public JobKey getJobKey() {
		return builder.getJobKey();
	}

	public DagFlow flow(String clusterName, String groupName, String jobName, String jobClassName) {
		DagJob dagNode = new DagJob(clusterName, groupName, jobName, jobClassName, this);
		nextDags.add(dagNode);
		return dagNode;
	}

	public DagFlow fork(String clusterName, String groupName, String jobName, String jobClassName) {
		DagJob dagJob = new DagJob(clusterName, groupName, jobName, jobClassName, null);
		forkDags.add(dagJob);
		return dagJob;
	}

	public JobDefinition[] getJobDefinitions() {
		List<JobDefinition> jobDefinitions = new ArrayList<JobDefinition>();
		List<JobKey> jobKeys = new ArrayList<JobKey>();
		for (DagJob forkDag : forkDags) {
			jobKeys.add(forkDag.getJobKey());
			jobDefinitions.addAll(Arrays.asList(forkDag.getJobDefinitions()));
		}
		builder.setForkKeys(jobKeys.toArray(new JobKey[0]));
		if (previous != null) {
			builder.setDependentKeys(new JobKey[] { previous.getJobKey() });
		}
		jobDefinitions.add(builder.build());
		if (nextDags.size() > 0) {
			for (DagDefination nextDag : nextDags) {
				jobDefinitions.addAll(Arrays.asList(nextDag.getJobDefinitions()));
			}
		}
		return jobDefinitions.toArray(new JobDefinition[0]);
	}

}
