package indi.atlantis.framework.jobhub.model;

import indi.atlantis.framework.jobhub.TriggerType;
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
