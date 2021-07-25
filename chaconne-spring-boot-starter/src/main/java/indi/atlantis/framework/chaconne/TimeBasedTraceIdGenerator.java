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
package indi.atlantis.framework.chaconne;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.github.paganini2008.springdessert.reditools.common.TimeBasedIdGenerator;

/**
 * 
 * TimeBasedTraceIdGenerator
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class TimeBasedTraceIdGenerator implements TraceIdGenerator {

	public TimeBasedTraceIdGenerator(RedisConnectionFactory connectionFactory) {
		this.idGen = new TimeBasedIdGenerator("JobTraceId:", connectionFactory);
	}

	private final TimeBasedIdGenerator idGen;

	@Override
	public long generateTraceId(JobKey jobKey) {
		return idGen.generateId();
	}

}
