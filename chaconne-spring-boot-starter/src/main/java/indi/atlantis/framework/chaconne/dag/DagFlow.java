package indi.atlantis.framework.chaconne.dag;

/**
 * 
 * DagFlow
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public interface DagFlow extends DagNode {

	DagFlow flow(String clusterName, String groupName, String jobClassName, String jobName);

	DagFlow fork(String clusterName, String groupName, String jobClassName, String jobName);

}