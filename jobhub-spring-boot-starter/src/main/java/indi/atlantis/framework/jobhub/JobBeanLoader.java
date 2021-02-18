package indi.atlantis.framework.jobhub;

/**
 * 
 * JobBeanLoader
 *
 * @author Jimmy Hoff
 */
public interface JobBeanLoader {

	Job loadJobBean(JobKey jobKey) throws Exception;

}
