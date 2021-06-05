package indi.atlantis.framework.chaconne.dag;

import indi.atlantis.framework.chaconne.JobDefinition;
import indi.atlantis.framework.chaconne.JobKey;

/**
 * 
 * DagDefination
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface DagDefination {

	JobKey getJobKey();

	JobDefinition[] getJobDefinitions();

}
