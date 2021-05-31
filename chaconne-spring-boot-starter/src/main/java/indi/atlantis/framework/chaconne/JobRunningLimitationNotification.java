package indi.atlantis.framework.chaconne;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.chaconne.model.JobDetail;

/**
 * 
 * JobRunningLimitationNotification
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class JobRunningLimitationNotification extends JobConditionalTermination {

	@Autowired
	private JobQueryDao jobQueryDao;

	@Autowired
	private JobManager jobManager;

	@Override
	protected boolean apply(long traceId, JobKey jobKey, Object attachment, Date startDate) {
		JobDetail jobDetail;
		try {
			jobDetail = jobManager.getJobDetail(jobKey, true);
		} catch (Exception e) {
			throw ExceptionUtils.wrapExeception(e);
		}
		int repeatCount = jobDetail.getJobTriggerDetail().getRepeatCount();
		if (repeatCount > 0) {
			int runningCount = jobQueryDao.selectJobRunningCount(jobDetail.getJobId());
			return runningCount >= repeatCount;
		}
		return false;
	}

}
