/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 
 * JobIdCache
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class JobIdCache {

	public JobIdCache(RedisConnectionFactory redisConnectionFactory, RedisSerializer<?> redisSerializer) {
		redisTemplate = new RedisTemplate<JobKey, Integer>();
		redisTemplate.setKeySerializer(redisSerializer);
		redisTemplate.setValueSerializer(new GenericToStringSerializer<Integer>(Integer.class));
		redisTemplate.setExposeConnection(true);
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.afterPropertiesSet();
	}

	private final RedisTemplate<JobKey, Integer> redisTemplate;

	public int getJobId(JobKey jobKey, JobIdSupplier supplier) throws SQLException {
		if (!redisTemplate.hasKey(jobKey)) {
			redisTemplate.opsForValue().set(jobKey, supplier.get(), 1, TimeUnit.HOURS);
		}
		return redisTemplate.opsForValue().get(jobKey).intValue();
	}

	public void evict(JobKey jobKey) {
		if (redisTemplate.hasKey(jobKey)) {
			redisTemplate.delete(jobKey);
		}
	}

	@FunctionalInterface
	public interface JobIdSupplier {

		int get() throws SQLException;

	}

}
