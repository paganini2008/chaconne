package indi.atlantis.framework.chaconne;

import indi.atlantis.framework.tridenter.utils.ApplicationContextUtils;

/**
 * 
 * JobUtils
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public abstract class JobUtils {

	public static Job getJobBean(String jobName, Class<?> jobClass) {
		Job job = (Job) ApplicationContextUtils.getBean(jobName, jobClass);
		if (job == null) {
			job = (Job) ApplicationContextUtils.getBean(jobClass, bean -> {
				return ((Job) bean).getJobName().equals(jobName);
			});
		}
		return job;
	}

}
