package indi.atlantis.framework.chaconne;

import java.util.Date;

import indi.atlantis.framework.chaconne.model.TriggerDescription;

/**
 * 
 * Trigger
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface Trigger {

	Date getEndDate();

	Date getStartDate();
	
	int getRepeatCount();

	TriggerType getTriggerType();

	TriggerDescription getTriggerDescription();

}
