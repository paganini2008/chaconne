package org.springtribe.framework.jobslacker.model;

import org.springtribe.framework.jobslacker.TriggerType;

import lombok.Data;

/**
 * 
 * JobKeyQuery
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Data
public class JobKeyQuery {

	private String clusterName;
	private TriggerType triggerType;

}
