package indi.atlantis.framework.chaconne;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.github.paganini2008.springworld.reditools.common.TimeBasedIdGenerator;

/**
 * 
 * TimeBasedTraceIdGenerator
 * 
 * @author Jimmy Hoff
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
