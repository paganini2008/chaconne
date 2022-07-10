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

import org.springframework.web.client.RestClientException;

import com.github.paganini2008.devtools.multithreads.Retryable;

import io.atlantisframework.chaconne.JobException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * RestClientRetryable
 *
 * @author Fred Feng
 *
 * @since 2.0.3
 */
@Slf4j
public abstract class RestClientRetryable implements Retryable {

	public static final int DEFAULT_RETRY_INTERVAL = 5;

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Exception>[] captureClasses() {
		return new Class[] { RestClientException.class, JobServiceAccessException.class };
	}

	@Override
	public void onError(int count, Throwable e) {
		if (count > 0) {
			if (log.isWarnEnabled()) {
				log.warn("[Unavailable Chaconne Center]: retry: {}, {}", count, e.getMessage());
			}
		} else {
			throw new JobException(e.getMessage(), e);
		}
	}

}
