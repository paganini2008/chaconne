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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpRequestFactory;

import com.github.paganini2008.devtools.ArrayUtils;

/**
 * 
 * ProducerModeRestTemplate
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class ProducerModeRestTemplate extends ClusterRestTemplate {

	public ProducerModeRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		super(httpRequestFactory);
	}

	@Autowired
	private JobServerRegistry serverRegistry;

	@Value("${atlantis.framework.chaconne.scheduler.running.mode}")
	private String runningMode;

	@Autowired
	private ContextPathAccessor contextPathAccessor;

	@Override
	protected String[] getClusterContextPaths(String clusterName) {
		String[] contextPaths = serverRegistry.getClusterContextPaths(clusterName);
		if (ArrayUtils.isNotEmpty(contextPaths)) {
			if ("master-slave".equals(runningMode)) {
				contextPaths = new String[] { contextPaths[0] };
			}
		}
		return contextPaths;
	}

	@Override
	protected boolean canAccessContextPath(String contextPath) {
		return contextPathAccessor.canAccess(contextPath);
	}

	@Override
	protected void invalidateContextPath(String contextPath) {
		contextPathAccessor.watchContextPath(contextPath);
	}

}
