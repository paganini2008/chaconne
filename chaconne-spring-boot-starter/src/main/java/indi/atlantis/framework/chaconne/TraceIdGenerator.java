package indi.atlantis.framework.chaconne;

/**
 * 
 * TraceIdGenerator
 * 
 * @author Fred Feng
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
