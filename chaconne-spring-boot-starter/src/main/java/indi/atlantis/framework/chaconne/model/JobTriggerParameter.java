package indi.atlantis.framework.chaconne.model;

import java.util.Date;

import indi.atlantis.framework.chaconne.SchedulingUnit;
import indi.atlantis.framework.chaconne.TriggerType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * JobTriggerParameter
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
@ToString
public class JobTriggerParameter {

	private TriggerType triggerType;
	private TriggerDescription triggerDescription;
	private Date startDate;
	private Date endDate;
	private int repeatCount = -1;

	public JobTriggerParameter() {
		this.triggerDescription = new TriggerDescription();
		this.triggerType = TriggerType.SIMPLE;
	}

	public JobTriggerParameter(String cronExpression) {
		this.triggerDescription = new TriggerDescription(cronExpression);
		this.triggerType = TriggerType.CRON;
	}

	public JobTriggerParameter(long period, SchedulingUnit schedulingUnit, boolean fixedRate) {
		this.triggerDescription = new TriggerDescription(period, schedulingUnit, fixedRate);
		this.triggerType = TriggerType.PERIODIC;
	}

}
