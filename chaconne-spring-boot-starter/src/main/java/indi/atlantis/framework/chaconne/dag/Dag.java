package indi.atlantis.framework.chaconne.dag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import indi.atlantis.framework.chaconne.JobDefinition;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.Trigger;
import indi.atlantis.framework.chaconne.utils.GenericJobDefinition;

/**
 * 
 * Dag
 * 
 * @author Fred Feng
 *
 * @version 1.0
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
