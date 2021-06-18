/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.net.NetUtils;

import indi.atlantis.framework.tridenter.InstanceId;

/**
 * 
 * JdbcStopWatch
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class JdbcStopWatch implements StopWatch {

	@Autowired
	private InstanceId instanceId;

	@Value("${server.port}")
	private int port;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobFutureHolder jobFutureHolder;

	@Autowired
	private JobDao jobDao;

	@Override
	public JobState onJobBegin(long traceId, JobKey jobKey, Date startDate) {
		try {
			long nextExecutionTime = jobFutureHolder.hasKey(jobKey)
					? jobFutureHolder.get(jobKey).getNextExectionTime(startDate, startDate, startDate)
					: -1L;
			Map<String, Object> kwargs = new HashMap<String, Object>();
			kwargs.put("jobState", JobState.RUNNING.getValue());
			kwargs.put("lastExecutionTime", new Timestamp(startDate.getTime()));
			kwargs.put("nextExecutionTime", nextExecutionTime > 0 ? new Timestamp(nextExecutionTime) : null);
			kwargs.put("clusterName", jobKey.getClusterName());
			kwargs.put("groupName", jobKey.getGroupName());
			kwargs.put("jobName", jobKey.getJobName());
			kwargs.put("jobClassName", jobKey.getJobClassName());
			jobDao.updateJobRunningBegin(kwargs);
			return JobState.RUNNING;
		} catch (Exception e) {
			throw new JobException(e.getMessage(), e);
		}
	}

	@Override
	public JobState onJobEnd(long traceId, JobKey jobKey, Date startDate, RunningState runningState, int retries) {
		final int jobId = getJobId(jobKey);
		final Date endTime = new Date();
		try {
			Map<String, Object> kwargs = new HashMap<String, Object>();
			kwargs.put("jobState", JobState.SCHEDULING.getValue());
			kwargs.put("lastRunningState", runningState.getValue());
			kwargs.put("lastCompletionTime", endTime);
			kwargs.put("clusterName", jobKey.getClusterName());
			kwargs.put("groupName", jobKey.getGroupName());
			kwargs.put("jobName", jobKey.getJobName());
			kwargs.put("jobClassName", jobKey.getJobClassName());
			jobDao.updateJobRunningEnd(kwargs);
			
			int complete = 0, failed = 0, skipped = 0, finished = 0;
			switch (runningState) {
			case COMPLETED:
				complete = 1;
				break;
			case FAILED:
				failed = 1;
				break;
			case SKIPPED:
				skipped = 1;
				break;
			case FINISHED:
				finished = 1;
				break;
			default:
				break;
			}
			
			kwargs = new HashMap<String, Object>();
			kwargs.put("traceId", traceId);
			kwargs.put("jobId", jobId);
			kwargs.put("runningState", runningState.getValue());
			kwargs.put("address", getSelfAddress());
			kwargs.put("instanceId", instanceId.get());
			kwargs.put("completed", complete);
			kwargs.put("failed", failed);
			kwargs.put("skipped", skipped);
			kwargs.put("finished", finished);
			kwargs.put("retries", retries);
			kwargs.put("executionTime", startDate);
			kwargs.put("completionTime", endTime);
			jobDao.saveJobTrace(kwargs);
			return JobState.SCHEDULING;
		} catch (Exception e) {
			throw new JobException(e.getMessage(), e);
		}

	}

	protected String getSelfAddress() {
		return NetUtils.getLocalHost() + ":" + port;
	}

	private int getJobId(JobKey jobKey) {
		try {
			return jobManager.getJobId(jobKey);
		} catch (Exception e) {
			throw ExceptionUtils.wrapExeception(e);
		}
	}

}
