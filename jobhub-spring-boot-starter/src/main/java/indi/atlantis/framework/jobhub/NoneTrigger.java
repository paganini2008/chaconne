package indi.atlantis.framework.jobhub;

import java.util.Date;

import indi.atlantis.framework.jobhub.model.TriggerDescription;

/**
 * 
 * NoneTrigger
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class NoneTrigger implements Trigger {

	private final TriggerDescription triggerDescription;

	public NoneTrigger() {
		this.triggerDescription = new TriggerDescription();
	}

	@Override
	public Date getEndDate() {
		return null;
	}

	@Override
	public Date getStartDate() {
		return null;
	}

	@Override
	public int getRepeatCount() {
		return -1;
	}

	@Override
	public TriggerType getTriggerType() {
		return TriggerType.NONE;
	}

	@Override
	public TriggerDescription getTriggerDescription() {
		return triggerDescription;
	}

}
