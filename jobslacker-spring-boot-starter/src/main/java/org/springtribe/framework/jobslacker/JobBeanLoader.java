package org.springtribe.framework.jobslacker;

/**
 * 
 * JobBeanLoader
 *
 * @author Jimmy Hoff
 */
public interface JobBeanLoader {

	Job loadJobBean(JobKey jobKey) throws Exception;

}
