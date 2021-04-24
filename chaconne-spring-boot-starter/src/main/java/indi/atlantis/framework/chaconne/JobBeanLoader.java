package indi.atlantis.framework.chaconne;

/**
 * 
 * JobBeanLoader
 *
 * @author Jimmy Hoff
 */
public interface JobBeanLoader {

	Job loadJobBean(JobKey jobKey) throws Exception;

}
