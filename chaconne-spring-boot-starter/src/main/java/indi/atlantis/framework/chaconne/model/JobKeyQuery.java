package indi.atlantis.framework.chaconne.model;

import indi.atlantis.framework.chaconne.TriggerType;
import lombok.Data;

/**
 * 
 * JobKeyQuery
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Data
public class JobKeyQuery {

	private String clusterName;
	private String groupName;
	private String clusterNames;
	private String groupNames;
	private TriggerType triggerType;

}
