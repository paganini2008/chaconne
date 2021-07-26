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
package indi.atlantis.framework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.LogLevel;
import indi.atlantis.framework.chaconne.LogManager;
import indi.atlantis.framework.chaconne.model.JobLogParameter;
import indi.atlantis.framework.chaconne.model.Result;

/**
 * 
 * RestLogManager
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public class RestLogManager implements LogManager {

	@Autowired
	private ClusterRestTemplate restTemplate;

	@Async
	@Override
	public void log(long traceId, JobKey jobKey, LogLevel logLevel, String messagePattern, Object[] args, String[] stackTraces) {
		ResponseEntity<Result<String>> responseEntity = restTemplate.perform(jobKey.getClusterName(), "/job/manager/log",
				HttpMethod.POST, new JobLogParameter(traceId, jobKey, logLevel, messagePattern, args, stackTraces),
				new ParameterizedTypeReference<Result<String>>() {
				});
		responseEntity.getBody().getData();
	}

	@Override
	public void log(long traceId, LogLevel level, JobKey jobKey, String msg, String[] stackTraces) {
		throw new UnsupportedOperationException("log");
	}

	@Override
	public void error(long traceId, JobKey jobKey, String msg, String[] stackTraces) {
		throw new UnsupportedOperationException("error");
	}

}
