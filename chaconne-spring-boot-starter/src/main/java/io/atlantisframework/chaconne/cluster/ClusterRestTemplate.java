/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.atlantisframework.chaconne.cluster;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.paganini2008.devtools.ArrayUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ClusterRestTemplate
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public abstract class ClusterRestTemplate extends RestTemplate {

	public ClusterRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		super(httpRequestFactory);
	}

	public ClusterRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	private final MultiValueMap<String, String> defaultHeaders = new LinkedMultiValueMap<String, String>();

	public void addHeader(String headerName, String headerValue) {
		defaultHeaders.add(headerName, headerValue);
	}

	public void setHeader(String headerName, String headerValue) {
		defaultHeaders.set(headerName, headerValue);
	}

	private boolean loadBalanceEnabled = true;

	public void setLoadBalanceEnabled(boolean loadBalanceEnabled) {
		this.loadBalanceEnabled = loadBalanceEnabled;
	}

	private ContextPathSelector contextPathSelector = new DefaultContextPathAccessor();

	public void setContextPathSelector(ContextPathSelector contextPathSelector) {
		this.contextPathSelector = contextPathSelector;
	}

	public <R> ResponseEntity<R> perform(String clusterName, String path, HttpMethod method, Object body,
			ParameterizedTypeReference<R> responseType) {
		final String[] contextPaths = getClusterContextPaths(clusterName);
		if (ArrayUtils.isEmpty(contextPaths)) {
			throw new UnavailableJobServiceException(clusterName);
		}
		RestClientException reason = null;
		if (loadBalanceEnabled) {
			String[] copy = contextPaths.clone();
			String contextPath = null, url = null;
			while (copy.length > 0) {
				contextPath = contextPathSelector.selectContextPath(clusterName, copy);
				if (!canAccessContextPath(contextPath)) {
					copy = ArrayUtils.remove(copy, contextPath);
					continue;
				}
				url = contextPath + path;
				if (log.isTraceEnabled()) {
					log.trace("Perform job with url: " + url);
				}
				try {
					return super.exchange(url, method, new HttpEntity<Object>(body, getHttpHeaders()), responseType);
				} catch (RestClientException e) {
					reason = e;
					copy = ArrayUtils.remove(copy, contextPath);
					invalidateContextPath(contextPath);
				}
			}
			throw reason != null ? new JobServiceAccessException(contextPaths, reason) : new JobServiceAccessException(contextPaths);
		} else {
			String url = null;
			for (String contextPath : contextPaths) {
				if (!canAccessContextPath(contextPath)) {
					continue;
				}
				url = contextPath + path;
				if (log.isTraceEnabled()) {
					log.trace("Perform job with url: " + url);
				}
				try {
					return super.exchange(url, method, new HttpEntity<Object>(body, getHttpHeaders()), responseType);
				} catch (RestClientException e) {
					reason = e;
					invalidateContextPath(contextPath);
				}
			}
			throw reason != null ? new JobServiceAccessException(contextPaths, reason) : new JobServiceAccessException(contextPaths);
		}

	}

	protected abstract String[] getClusterContextPaths(String clusterName);

	protected boolean canAccessContextPath(String contextPath) {
		return true;
	}

	protected void invalidateContextPath(String contextPath) {
	}

	protected HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.addAll(defaultHeaders);
		return headers;
	}

}
