package indi.atlantis.framework.jobby.model;

import indi.atlantis.framework.jobby.TriggerType;
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
