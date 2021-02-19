package indi.atlantis.framework.jobby;

/**
 * 
 * TraceIdGenerator
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public interface TraceIdGenerator {

	long generateTraceId(JobKey jobKey);

	static final TraceIdGenerator NOOP = new TraceIdGenerator() {

		@Override
		public long generateTraceId(JobKey jobKey) {
			return 0L;
		}
	};

}
