package indi.atlantis.framework.chaconne;

import org.slf4j.Logger;

import indi.atlantis.framework.chaconne.model.JobTriggerDetail;

/**
 * 
 * ExternalJobBeanProxy
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class ExternalJobBeanProxy implements Job {

	private final JobKey jobKey;
	private final JobTriggerDetail triggerDetail;

	public ExternalJobBeanProxy(JobKey jobKey, JobTriggerDetail triggerDetail) {
		this.jobKey = jobKey;
		this.triggerDetail = triggerDetail;
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
	public String getClusterName() {
		return jobKey.getClusterName();
	}

	@Override
	public Trigger getTrigger() {
		return GenericTrigger.Builder.newTrigger().setTriggerType(triggerDetail.getTriggerType())
				.setTriggerDescription(triggerDetail.getTriggerDescriptionObject()).setStartDate(triggerDetail.getStartDate())
				.setEndDate(triggerDetail.getEndDate()).setRepeatCount(triggerDetail.getRepeatCount()).build();
	}

	@Override
	public Object execute(JobKey jobKey, Object result, Logger log) {
		throw new UnsupportedOperationException("execute");
	}

}
