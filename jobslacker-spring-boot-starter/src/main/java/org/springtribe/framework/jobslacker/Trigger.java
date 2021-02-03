package org.springtribe.framework.jobslacker;

import java.util.Date;

import org.springtribe.framework.jobslacker.model.TriggerDescription;

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
