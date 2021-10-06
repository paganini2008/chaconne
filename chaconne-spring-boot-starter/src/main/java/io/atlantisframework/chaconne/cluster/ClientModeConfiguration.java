/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

import io.atlantisframework.chaconne.JobAdmin;
import io.atlantisframework.chaconne.JobManager;

/**
 * 
 * ClientModeConfiguration
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@ConditionalOnWebApplication
@Configuration(proxyBeanMethods = false)
public class ClientModeConfiguration {

	@Bean
	public ClusterRestTemplate clusterRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
		return new ClientModeClusterRestTemplate(httpRequestFactory);
	}

	@Bean
	@ConditionalOnMissingBean
	public JobManager jobManager() {
		return new RestJobManager();
	}

	@Bean
	@ConditionalOnMissingBean
	public JobAdmin jobAdmin() {
		return new RestJobAdmin();
	}

	@Bean
	public ContextPathAccessor contextPathAccessor() {
		return new ContextPathAccessor();
	}
}
