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
