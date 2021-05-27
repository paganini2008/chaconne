package indi.atlantis.framework.chaconne;

import java.util.Date;

import indi.atlantis.framework.chaconne.model.TriggerDescription;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * PeriodicTrigger
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Accessors(chain = true)
@Getter
@Setter
public class PeriodicTrigger implements Trigger {

	private Date startDate;
	private Date endDate;
	private int repeatCount = -1;
	private final TriggerDescription triggerDescription;

	public PeriodicTrigger(int period, SchedulingUnit schedulingUnit, boolean fixedRate) {
		this.triggerDescription = new TriggerDescription(period, schedulingUnit, fixedRate);
	}

	public PeriodicTrigger(TriggerDescription triggerDescription) {
		this.triggerDescription = triggerDescription;
	}

	@Override
	public TriggerType getTriggerType() {
		return TriggerType.PERIODIC;
	}

}
