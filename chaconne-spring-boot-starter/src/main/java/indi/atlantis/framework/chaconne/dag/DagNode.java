package indi.atlantis.framework.chaconne.dag;

/**
 * 
 * DagNode
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface DagNode extends DagDefination {

	DagNode setDescription(String description);

	DagNode setRetries(int retries);

	DagNode setWeight(int weight);

	DagNode setTimeout(long timeout);

	DagDefination getPrevious();

	DagDefination[] getNext();

}