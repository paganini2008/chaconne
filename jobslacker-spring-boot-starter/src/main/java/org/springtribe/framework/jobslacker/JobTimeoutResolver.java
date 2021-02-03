package org.springtribe.framework.jobslacker;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springtribe.framework.cluster.election.ApplicationClusterLeaderEvent;
import org.springtribe.framework.cluster.utils.BeanLifeCycle;
import org.springtribe.framework.jobslacker.model.JobDetail;
import org.springtribe.framework.jobslacker.model.JobRuntime;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.jdbc.ConnectionFactory;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.multithreads.AtomicIntegerSequence;
import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobTimeoutResolver
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class JobTimeoutResolver implements ApplicationListener<ApplicationClusterLeaderEvent>, Executable, BeanLifeCycle {

	private final Map<JobKey, AtomicIntegerSequence> counters = new ConcurrentHashMap<JobKey, AtomicIntegerSequence>();

	private Timer timer;

	@Autowired
	private ConnectionFactory connectionFactory;

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
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			List<Tuple> dataList = JdbcUtils.fetchAll(connection, SqlScripts.DEF_SELECT_JOB_RUNTIME_BY_JOB_STATE,
					new Object[] { JobState.RUNNING.getValue() });
			if (CollectionUtils.isNotEmpty(dataList)) {
				for (Tuple tuple : dataList) {
					JobDetail jobDetail = tuple.toBean(JobDetail.class);
					JobRuntime jobRuntime = tuple.toBean(JobRuntime.class);
					if ((jobDetail.getTimeout() > 0) && (jobRuntime.getLastExecutionTime() != null)
							&& (System.currentTimeMillis() - jobRuntime.getLastExecutionTime().getTime() > jobDetail.getTimeout())) {
						jobKeys.add(tuple.toBean(JobKey.class));
					}
				}
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
		return jobKeys;
	}

	private List<JobKey> getFrozenJobKeys() {
		List<JobKey> jobKeys = new ArrayList<JobKey>();
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			List<Tuple> dataList = JdbcUtils.fetchAll(connection, SqlScripts.DEF_SELECT_JOB_RUNTIME_BY_JOB_STATE,
					new Object[] { JobState.FROZEN.getValue() });
			if (CollectionUtils.isNotEmpty(dataList)) {
				for (Tuple tuple : dataList) {
					JobKey jobKey = tuple.toBean(JobKey.class);
					JobDetail jobDetail = tuple.toBean(JobDetail.class);
					JobRuntime jobRuntime = tuple.toBean(JobRuntime.class);
					if ((jobDetail.getTimeout() > 0) && (jobRuntime.getLastExecutionTime() != null) && (System.currentTimeMillis()
							- jobRuntime.getLastExecutionTime().getTime() > jobDetail.getTimeout() * getTimeoutInterval(jobKey).get())) {
						jobKeys.add(jobKey);
					}
				}
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
		return jobKeys;
	}

	@Override
	public void onApplicationEvent(ApplicationClusterLeaderEvent event) {
		this.timer = ThreadUtils.scheduleWithFixedDelay(this, 1, TimeUnit.MINUTES);
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
