package indi.atlantis.framework.chaconne.cluster;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.jdbc.PageRequest;
import com.github.paganini2008.devtools.jdbc.PageResponse;
import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

import indi.atlantis.framework.chaconne.Job;
import indi.atlantis.framework.chaconne.JobBeanInitializer;
import indi.atlantis.framework.chaconne.JobBeanLoader;
import indi.atlantis.framework.chaconne.JobKey;
import indi.atlantis.framework.chaconne.JobQueryDao;
import indi.atlantis.framework.chaconne.ScheduleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ProducerModeJobBeanInitializer
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class ProducerModeJobBeanInitializer implements JobBeanInitializer {

	@Autowired
	private JobQueryDao jobQueryDao;

	@Autowired
	private ScheduleManager scheduleManager;

	@Autowired
	private JobBeanLoader jobBeanLoader;

	public void initializeJobBeans() throws Exception {
		ResultSetSlice<Map<String, Object>> resultSetSlice = jobQueryDao.selectAllJobDetails();
		PageResponse<Map<String, Object>> pageResponse = resultSetSlice.list(PageRequest.of(1, 10));
		List<Map<String, Object>> dataList;
		JobKey jobKey;
		Job job;
		for (PageResponse<Map<String, Object>> page : pageResponse) {
			dataList = page.getContent();
			for (Map<String, Object> data : dataList) {
				jobKey = JobKey.of(data);
				try {
					job = jobBeanLoader.loadJobBean(jobKey);
				} catch (Exception e) {
					log.error("Unable to load Job Bean: {}", jobKey, e);
					continue;
				}
				if (job == null) {
					continue;
				}
				if (scheduleManager.hasScheduled(jobKey)) {
					continue;
				}
				scheduleManager.schedule(job);
			}
		}
	}

}
