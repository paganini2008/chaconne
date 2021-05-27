package indi.atlantis.framework.chaconne;

import java.util.Date;

import indi.atlantis.framework.chaconne.model.TriggerDescription;

/**
 * 
 * NoneTrigger
 * 
 * @author Fred Feng
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
