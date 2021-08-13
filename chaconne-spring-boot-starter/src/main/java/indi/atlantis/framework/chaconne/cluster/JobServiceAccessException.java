package indi.atlantis.framework.chaconne.cluster;

import org.springframework.web.client.RestClientException;

import com.github.paganini2008.devtools.net.Urls;

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

	public JobServiceAccessException(String url, RestClientException e) {
		super(e.getMessage(), e);
		this.url = url;
	}

	private final String url;

	public String getContextPath() {
		return Urls.toHostUrl(url).toString();
	}

	public String getUrl() {
		return url;
	}

}
