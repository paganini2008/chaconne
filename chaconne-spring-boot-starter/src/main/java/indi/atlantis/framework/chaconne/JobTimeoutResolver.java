/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package indi.atlantis.framework.chaconne;

import static com.github.paganini2008.devtools.beans.BeanUtils.convertAsBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.multithreads.AtomicIntegerSequence;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import indi.atlantis.framework.chaconne.model.JobDetail;
import indi.atlantis.framework.chaconne.model.JobRuntimeDetail;
import indi.atlantis.framework.tridenter.LeaderState;
import indi.atlantis.framework.tridenter.election.ApplicationClusterLeaderEvent;
import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobTimeoutResolver
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class JobTimeoutResolver implements ApplicationListener<ApplicationClusterLeaderEvent>, Executable, BeanLifeCycle {

	private final Map<JobKey, AtomicIntegerSequence> counters = new ConcurrentHashMap<JobKey, AtomicIntegerSequence>();

	private Timer timer;

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Autowired
	private JobQueryDao jobQueryDao;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private ScheduleManager scheduleManager;

	@Override
	public boolean execute() {
		unfreezeJobs();
		freezeJobs();
		return true;
	}

	private void unfreezeJobs() {
		List<JobKey> jobKeys = getFrozenJobKeys();
		try {
			for (JobKey jobKey : jobKeys) {
				jobManager.setJobState(jobKey, JobState.NOT_SCHEDULED);
				AtomicIntegerSequence sequence = getTimeoutInterval(jobKey);
				int interval = sequence.get();
				if (interval > 1) {
					interval = sequence.decrementAndGet();
				}
				log.info("Unfreeze job '{}', interval={}", jobKey, interval);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void freezeJobs() {
		List<JobKey> jobKeys = getRunningJobKeys();
		try {
			for (JobKey jobKey : jobKeys) {
				scheduleManager.unscheduleJob(jobKey);
				jobManager.setJobState(jobKey, JobState.FROZEN);
				int interval = getTimeoutInterval(jobKey).getAndIncrement();
				log.info("Freeze job '{}', interval={}", jobKey, interval);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private AtomicIntegerSequence getTimeoutInterval(JobKey jobKey) {
		return MapUtils.get(counters, jobKey, () -> {
			return new AtomicIntegerSequence(1, 100);
		});
	}

	private List<JobKey> getRunningJobKeys() {
		List<JobKey> jobKeys = new ArrayList<JobKey>();
		List<Map<String, Object>> dataList = jobQueryDao.selectJobRuntimeByJobState(clusterName, JobState.RUNNING.getValue());
		if (CollectionUtils.isNotEmpty(dataList)) {
			for (Map<String, Object> data : dataList) {
				JobDetail jobDetail = convertAsBean(data, JobDetail.class);
				JobRuntimeDetail jobRuntime = convertAsBean(data, JobRuntimeDetail.class);
				if ((jobDetail.getTimeout() > 0) && (jobRuntime.getLastExecutionTime() != null)
						&& (System.currentTimeMillis() - jobRuntime.getLastExecutionTime().getTime() > jobDetail.getTimeout())) {
					jobKeys.add(convertAsBean(data, JobKey.class));
				}
			}
		}
		return jobKeys;
	}

	private List<JobKey> getFrozenJobKeys() {
		List<JobKey> jobKeys = new ArrayList<JobKey>();
		List<Map<String, Object>> dataList = jobQueryDao.selectJobRuntimeByJobState(clusterName, JobState.FROZEN.getValue());
		if (CollectionUtils.isNotEmpty(dataList)) {
			for (Map<String, Object> data : dataList) {
				JobKey jobKey = convertAsBean(data, JobKey.class);
				JobDetail jobDetail = convertAsBean(data, JobDetail.class);
				JobRuntimeDetail jobRuntime = convertAsBean(data, JobRuntimeDetail.class);
				if ((jobDetail.getTimeout() > 0) && (jobRuntime.getLastExecutionTime() != null) && (System.currentTimeMillis()
						- jobRuntime.getLastExecutionTime().getTime() > jobDetail.getTimeout() * getTimeoutInterval(jobKey).get())) {
					jobKeys.add(jobKey);
				}
			}
		}
		return jobKeys;
	}

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		if (event.getLeaderState() == LeaderState.UP) {
			this.timer = ThreadUtils.scheduleWithFixedDelay(this, 1, TimeUnit.MINUTES);
		}
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
