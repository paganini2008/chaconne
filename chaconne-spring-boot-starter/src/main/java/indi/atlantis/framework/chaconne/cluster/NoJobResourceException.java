package indi.atlantis.framework.chaconne.cluster;

import indi.atlantis.framework.chaconne.JobException;

/**
 * 
 * NoJobResourceException
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class NoJobResourceException extends JobException {
	
	private static final long serialVersionUID = 3321347517738601224L;
	
	public NoJobResourceException(String clusterName) {
		super(clusterName);
	}

}
