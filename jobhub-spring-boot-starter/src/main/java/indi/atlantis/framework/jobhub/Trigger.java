package indi.atlantis.framework.jobhub;

import java.util.Date;

import indi.atlantis.framework.jobhub.model.TriggerDescription;

/**
 * 
 * Trigger
 * 
 * @author Jimmy Hoff
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
