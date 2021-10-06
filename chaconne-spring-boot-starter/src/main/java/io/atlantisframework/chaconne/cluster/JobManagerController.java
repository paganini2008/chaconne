/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package io.atlantisframework.chaconne.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.JobState;
import io.atlantisframework.chaconne.LogManager;
import io.atlantisframework.chaconne.StopWatch;
import io.atlantisframework.chaconne.TraceIdGenerator;
import io.atlantisframework.chaconne.model.JobDependencyParameter;
import io.atlantisframework.chaconne.model.JobDetail;
import io.atlantisframework.chaconne.model.JobKeyQuery;
import io.atlantisframework.chaconne.model.JobLog;
import io.atlantisframework.chaconne.model.JobLogParameter;
import io.atlantisframework.chaconne.model.JobPersistParameter;
import io.atlantisframework.chaconne.model.JobRuntimeDetail;
import io.atlantisframework.chaconne.model.JobRuntimeParameter;
import io.atlantisframework.chaconne.model.JobStackTrace;
import io.atlantisframework.chaconne.model.JobStat;
import io.atlantisframework.chaconne.model.JobStatDetail;
import io.atlantisframework.chaconne.model.JobStatPageQuery;
import io.atlantisframework.chaconne.model.JobStatQuery;
import io.atlantisframework.chaconne.model.JobStateCount;
import io.atlantisframework.chaconne.model.JobStateParameter;
import io.atlantisframework.chaconne.model.JobTrace;
import io.atlantisframework.chaconne.model.JobTracePageQuery;
import io.atlantisframework.chaconne.model.JobTraceQuery;
import io.atlantisframework.chaconne.model.JobTriggerDetail;
import io.atlantisframework.chaconne.model.PageQuery;
import io.atlantisframework.chaconne.model.Query;
import io.atlantisframework.chaconne.model.Result;

/**
 * 
 * JobManagerController
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@RestController
@RequestMapping("/job/manager")
public class JobManagerController {

	@Autowired
	private JobManager jobManager;

	@Autowired
	private LogManager logManager;

	@Autowired
	private StopWatch stopWatch;

	@Autowired
	private TraceIdGenerator traceIdGenerator;

	@GetMapping("/selectClusterNames")
	public ResponseEntity<Result<String[]>> selectClusterNames() throws Exception {
		String[] clusterNames = jobManager.selectClusterNames();
		return ResponseEntity.ok(Result.success(clusterNames));
	}

	@PostMapping("/persistJob")
	public ResponseEntity<Result<Integer>> persistJob(@RequestBody JobPersistParameter parameter) throws Exception {
		int jobId = jobManager.persistJob(parameter);
		return ResponseEntity.ok(Result.success(jobId));
	}

	@PostMapping("/finishJob")
	public ResponseEntity<Result<JobState>> finishJob(@RequestBody JobKey jobKey) throws Exception {
		JobState jobState = jobManager.finishJob(jobKey);
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/hasJob")
	public ResponseEntity<Result<Boolean>> hasJob(@RequestBody JobKey jobKey) throws Exception {
		boolean has = jobManager.hasJob(jobKey);
		return ResponseEntity.ok(Result.success(has));
	}

	@PostMapping("/pauseJob")
	public ResponseEntity<Result<JobState>> pauseJob(@RequestBody JobKey jobKey) throws Exception {
		JobState jobState = jobManager.pauseJob(jobKey);
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/resumeJob")
	public ResponseEntity<Result<JobState>> resumeJob(@RequestBody JobKey jobKey) throws Exception {
		JobState jobState = jobManager.resumeJob(jobKey);
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/hasJobState")
	public ResponseEntity<Result<Boolean>> hasJobState(@RequestBody JobStateParameter parameter) throws Exception {
		boolean has = jobManager.hasJobState(parameter.getJobKey(), parameter.getJobState());
		return ResponseEntity.ok(Result.success(has));
	}

	@PostMapping("/getJobId")
	public ResponseEntity<Result<Integer>> getJobId(@RequestBody JobKey jobKey) throws Exception {
		int jobId = jobManager.getJobId(jobKey);
		return ResponseEntity.ok(Result.success(jobId));
	}

	@PostMapping("/getJobDetail")
	public ResponseEntity<Result<JobDetail>> getJobDetail(@RequestBody JobKey jobKey) throws Exception {
		JobDetail jobDetail = jobManager.getJobDetail(jobKey, true);
		return ResponseEntity.ok(Result.success(jobDetail));
	}

	@PostMapping("/getJobTriggerDetail")
	public ResponseEntity<Result<JobTriggerDetail>> getJobTriggerDetail(@RequestBody JobKey jobKey) throws Exception {
		JobTriggerDetail jobTriggerDetail = jobManager.getJobTriggerDetail(jobKey);
		return ResponseEntity.ok(Result.success(jobTriggerDetail));
	}

	@PostMapping("/getJobRuntimeDetail")
	public ResponseEntity<Result<JobRuntimeDetail>> getJobRuntime(@RequestBody JobKey jobKey) throws Exception {
		JobRuntimeDetail jobRuntime = jobManager.getJobRuntimeDetail(jobKey);
		return ResponseEntity.ok(Result.success(jobRuntime));
	}

	@PostMapping("/hasRelations")
	public ResponseEntity<Result<Boolean>> hasRelations(@RequestBody JobDependencyParameter parameter) throws Exception {
		boolean result = jobManager.hasRelations(parameter.getJobKey(), parameter.getDependencyType());
		return ResponseEntity.ok(Result.success(result));
	}

	@PostMapping("/getRelations")
	public ResponseEntity<Result<JobKey[]>> getRelations(@RequestBody JobDependencyParameter parameter) throws Exception {
		JobKey[] jobKeys = jobManager.getRelations(parameter.getJobKey(), parameter.getDependencyType());
		return ResponseEntity.ok(Result.success(jobKeys));
	}

	@PostMapping("/getDependentKeys")
	public ResponseEntity<Result<JobKey[]>> getDependencies(@RequestBody JobDependencyParameter parameter) throws Exception {
		JobKey[] jobKeys = jobManager.getDependentKeys(parameter.getJobKey(), parameter.getDependencyType());
		return ResponseEntity.ok(Result.success(jobKeys));
	}

	@PostMapping("/setJobState")
	public ResponseEntity<Result<JobState>> setJobState(@RequestBody JobStateParameter parameter) throws Exception {
		JobState jobState = jobManager.setJobState(parameter.getJobKey(), parameter.getJobState());
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/getJobKeys")
	public ResponseEntity<Result<JobKey[]>> getJobKeys(@RequestBody JobKeyQuery jobQuery) throws Exception {
		JobKey[] jobKeys = jobManager.getJobKeys(jobQuery);
		return ResponseEntity.ok(Result.success(jobKeys));
	}

	@PostMapping("/selectJobDetail")
	public ResponseEntity<Result<PageQuery<JobDetail>>> selectJobDetail(@RequestBody PageQuery<JobDetail> pageQuery) throws Exception {
		jobManager.selectJobDetail(pageQuery);
		return ResponseEntity.ok(Result.success(pageQuery));
	}

	@PostMapping("/selectJobTrace")
	public ResponseEntity<Result<PageQuery<JobTrace>>> selectJobTrace(@RequestBody JobTracePageQuery<JobTrace> pageQuery) throws Exception {
		jobManager.selectJobTrace(pageQuery);
		return ResponseEntity.ok(Result.success(pageQuery));
	}

	@PostMapping("/selectJobLog")
	public ResponseEntity<Result<JobLog[]>> selectJobLog(@RequestBody JobTraceQuery query) throws Exception {
		JobLog[] logs = jobManager.selectJobLog(query);
		return ResponseEntity.ok(Result.success(logs));
	}

	@PostMapping("/selectJobStackTrace")
	public ResponseEntity<Result<JobStackTrace[]>> selectJobStackTrace(@RequestBody JobTraceQuery query) throws Exception {
		JobStackTrace[] traces = jobManager.selectJobStackTrace(query);
		return ResponseEntity.ok(Result.success(traces));
	}

	@PostMapping("/onJobBegin")
	public ResponseEntity<Result<JobState>> onJobBegin(@RequestBody JobRuntimeParameter parameter) throws Exception {
		JobState jobState = stopWatch.onJobBegin(parameter.getTraceId(), parameter.getJobKey(), parameter.getStartTime());
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/onJobEnd")
	public ResponseEntity<Result<JobState>> onJobEnd(@RequestBody JobRuntimeParameter parameter) throws Exception {
		JobState jobState = stopWatch.onJobEnd(parameter.getTraceId(), parameter.getJobKey(), parameter.getStartTime(),
				parameter.getRunningState(), parameter.getRetries());
		return ResponseEntity.ok(Result.success(jobState));
	}

	@PostMapping("/generateTraceId")
	public ResponseEntity<Result<Long>> generateTraceId(@RequestBody JobKey jobKey) {
		long traceId = traceIdGenerator.generateTraceId(jobKey);
		return ResponseEntity.ok(Result.success(traceId));
	}

	@PostMapping("/log")
	public ResponseEntity<Result<String>> log(@RequestBody JobLogParameter parameter) throws Exception {
		logManager.log(parameter.getTraceId(), parameter.getJobKey(), parameter.getLogLevel(), parameter.getMessagePattern(),
				parameter.getArgs(), parameter.getStackTraces());
		return ResponseEntity.ok(Result.success("ok"));
	}

	@PostMapping("/selectJobStatByDay")
	public ResponseEntity<Result<JobStatDetail[]>> selectJobStatByDay(@RequestBody JobStatQuery query) throws Exception {
		JobStatDetail[] jobStats = jobManager.selectJobStatByDay(query);
		return ResponseEntity.ok(Result.success(jobStats));
	}

	@PostMapping("/selectJobStatByMonth")
	public ResponseEntity<Result<JobStatDetail[]>> selectJobStatByMonth(@RequestBody JobStatQuery query) throws Exception {
		JobStatDetail[] jobStats = jobManager.selectJobStatByMonth(query);
		return ResponseEntity.ok(Result.success(jobStats));
	}

	@PostMapping("/selectJobStatById")
	public ResponseEntity<Result<PageQuery<JobStatDetail>>> selectJobStatById(@RequestBody JobStatPageQuery<JobStatDetail> query)
			throws Exception {
		jobManager.selectJobStatById(query);
		return ResponseEntity.ok(Result.success(query));
	}

	@PostMapping("/selectJobStateCount")
	public ResponseEntity<Result<JobStateCount[]>> selectJobStateCount(@RequestBody Query query) throws Exception {
		JobStateCount[] stateCounts = jobManager.selectJobStateCount(query);
		return ResponseEntity.ok(Result.success(stateCounts));
	}

	@PostMapping("/selectJobStat")
	public ResponseEntity<Result<JobStat>> selectJobStat(@RequestBody JobStatQuery query) throws Exception {
		JobStat jobStat = jobManager.selectJobStat(query);
		return ResponseEntity.ok(Result.success(jobStat));
	}

}
