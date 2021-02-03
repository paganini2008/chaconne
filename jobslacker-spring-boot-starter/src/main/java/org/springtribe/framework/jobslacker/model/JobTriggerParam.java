package org.springtribe.framework.jobslacker.model;

import java.util.Date;

import org.springtribe.framework.jobslacker.SchedulingUnit;
import org.springtribe.framework.jobslacker.TriggerType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * JobTriggerParam
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Getter
@Setter
@ToString
public class JobTriggerParam {

	private TriggerType triggerType;
	private TriggerDescription triggerDescription;
	private Date startDate;
	private Date endDate;
	private int repeatCount = -1;

	public JobTriggerParam() {
		this.triggerDescription = new TriggerDescription();
		this.triggerType = TriggerType.NONE;
	}

	public JobTriggerParam(String cronExpression) {
		this.triggerDescription = new TriggerDescription(cronExpression);
		this.triggerType = TriggerType.CRON;
	}

	public JobTriggerParam(long period, SchedulingUnit schedulingUnit, boolean fixedRate) {
		this.triggerDescription = new TriggerDescription(period, schedulingUnit, fixedRate);
		this.triggerType = TriggerType.PERIODIC;
	}

}
