package indi.atlantis.framework.chaconne.model;

import indi.atlantis.framework.chaconne.TriggerType;
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
