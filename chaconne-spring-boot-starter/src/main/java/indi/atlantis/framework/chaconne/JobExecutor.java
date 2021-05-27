package indi.atlantis.framework.chaconne;

/**
 * 
 * JobExecutor
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public interface JobExecutor {

	void execute(Job job, Object attachment, int retries);

}
