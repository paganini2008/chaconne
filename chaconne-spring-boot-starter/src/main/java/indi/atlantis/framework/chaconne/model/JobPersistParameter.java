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
		JobPersistParameter param = new JobPersistParameter();
		param.setJobKey(JobKey.by("yourCluster", "yourGroup", "yourJob", "com.yourcompany.yourapp.YourJob"));
		param.setDescription("Describe your job shortly");
		param.setEmail("Set your email if job run abnormally");
		param.setRetries(0);
		param.setTimeout(-1L);
		param.setAttachment("Set initial parameter of your job. Data with json format is recommended.");
		param.setTrigger(new JobTriggerParameter("*/5 * * * * ?"));
		return param;
	}

	public static JobPersistParameter wrap(JobDetail jobDetail) {
		JobPersistParameter param = new JobPersistParameter(jobDetail.getJobKey());
		param.setAttachment(jobDetail.getAttachment());
		param.setDescription(jobDetail.getDescription());
		param.setEmail(jobDetail.getEmail());
		param.setRetries(jobDetail.getRetries());
		param.setTimeout(jobDetail.getTimeout());
		param.setWeight(jobDetail.getWeight());

		JobTriggerDetail triggerDetail = jobDetail.getJobTriggerDetail();
		TriggerDescription triggerDescription = triggerDetail.getTriggerDescriptionObject();
		Dependency dependency = triggerDescription.getDependency();
		if (dependency != null) {
			param.setCompletionRate(dependency.getCompletionRate());
			param.setDependentKeys(dependency.getDependentKeys());
			param.setForkKeys(dependency.getForkKeys());
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
		param.setTrigger(trigger);

		return param;
	}

}
