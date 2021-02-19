package indi.atlantis.framework.jobby;

/**
 * 
 * JobBeanLoader
 *
 * @author Jimmy Hoff
 */
public interface JobBeanLoader {

	Job loadJobBean(JobKey jobKey) throws Exception;

}
