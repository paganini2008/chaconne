package org.springtribe.framework.jobslacker.server;

import org.springtribe.framework.jobslacker.JobException;

/**
 * 
 * NoJobResourceException
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class NoJobResourceException extends JobException {
	
	private static final long serialVersionUID = 3321347517738601224L;
	
	public NoJobResourceException(String clusterName) {
		super(clusterName);
	}

}
