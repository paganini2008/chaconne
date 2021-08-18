package indi.atlantis.framework.chaconne.cluster;

import org.springframework.web.client.RestClientException;

import com.github.paganini2008.devtools.ArrayUtils;

import indi.atlantis.framework.chaconne.JobException;

/**
 * 
 * JobServiceAccessException
 *
 * @author Fred Feng
 *
 * @since 2.0.3
 */
public class JobServiceAccessException extends JobException {

	private static final long serialVersionUID = 6741654358476739898L;

	public JobServiceAccessException(String[] contextPaths) {
		super(ArrayUtils.toString(contextPaths));
		this.contextPaths = contextPaths;
	}

	public JobServiceAccessException(String[] contextPaths, RestClientException e) {
		super(e.getMessage(), e);
		this.contextPaths = contextPaths;
	}

	private final String[] contextPaths;

	public String[] getContextPaths() {
		return contextPaths;
	}

}
