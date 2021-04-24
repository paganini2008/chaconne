package indi.atlantis.framework.chaconne;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;

/**
 * 
 * JobDeadlineNotification
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class JobDeadlineNotification implements JobListener, Executable, BeanLifeCycle {

	private final Map<JobKey, Date> deadlines = new ConcurrentHashMap<JobKey, Date>();
	private Timer timer;

	@Autowired
	private JobQueryDao jobQueryDao;

	@Override
	public void configure() throws Exception {
		refresh();
		this.timer = ThreadUtils.scheduleAtFixedRate(this, 1, TimeUnit.MINUTES);
	}

	@Override
	public void beforeRun(long traceId, JobKey jobKey, Object attachment, Date startDate) {
		Date theDeadline = deadlines.get(jobKey);
		if (theDeadline != null && theDeadline.before(startDate)) {
			deadlines.remove(jobKey);
			throw new JobTerminationException(jobKey, "Job '" + jobKey + "' has terminated on deadline: " + theDeadline);
		}
	}

	@Override
	public boolean execute() {
		refresh();
		return true;
	}

	private void refresh() {
		List<Map<String, Object>> dataList = jobQueryDao.selectJobTriggerDeadlines();
		JobKey jobKey;
		Date endDate;
		for (Map<String, Object> data : dataList) {
			jobKey = JobKey.of(data);
			endDate = (Date) data.get("endDate");
			deadlines.put(jobKey, endDate);
		}
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
		}
	}

}
