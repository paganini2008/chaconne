package indi.atlantis.framework.chaconne.cluster;

import org.springframework.web.client.RestClientException;

import com.github.paganini2008.devtools.StringUtils;
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

	public JobServiceAccessException(String msg) {
		super(msg);
		this.url = null;
	}

	public JobServiceAccessException(String url, RestClientException e) {
		super(e.getMessage(), e);
		this.url = url;
	}

	private final String url;

	public String getContextPath() {
		if (StringUtils.isBlank(url)) {
			return "";
		}
		return Urls.toHostUrl(url).toString();
	}

	public String getUrl() {
		return url;
	}

}
