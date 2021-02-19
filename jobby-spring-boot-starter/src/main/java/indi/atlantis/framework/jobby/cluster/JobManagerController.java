package indi.atlantis.framework.jobby.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import indi.atlantis.framework.jobby.JobKey;
import indi.atlantis.framework.jobby.JobManager;
import indi.atlantis.framework.jobby.JobState;
import indi.atlantis.framework.jobby.LogManager;
import indi.atlantis.framework.jobby.StopWatch;
import indi.atlantis.framework.jobby.TraceIdGenerator;
import indi.atlantis.framework.jobby.model.JobDependencyParam;
import indi.atlantis.framework.jobby.model.JobDetail;
import indi.atlantis.framework.jobby.model.JobKeyQuery;
import indi.atlantis.framework.jobby.model.JobLog;
import indi.atlantis.framework.jobby.model.JobLogParam;
import indi.atlantis.framework.jobby.model.JobPersistParam;
import indi.atlantis.framework.jobby.model.JobResult;
import indi.atlantis.framework.jobby.model.JobRuntime;
import indi.atlantis.framework.jobby.model.JobRuntimeParam;
import indi.atlantis.framework.jobby.model.JobStackTrace;
import indi.atlantis.framework.jobby.model.JobStateParam;
import indi.atlantis.framework.jobby.model.JobTrace;
import indi.atlantis.framework.jobby.model.JobTracePageQuery;
import indi.atlantis.framework.jobby.model.JobTraceQuery;
import indi.atlantis.framework.jobby.model.JobTriggerDetail;
import indi.atlantis.framework.jobby.model.PageQuery;

/**
 * 
 * JobManagerController
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
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
	public ResponseEntity<JobResult<String[]>> selectClusterNames() throws Exception {
		String[] clusterNames = jobManager.selectClusterNames();
		return ResponseEntity.ok(JobResult.success(clusterNames));
	}

	@PostMapping("/persistJob")
	public ResponseEntity<JobResult<Integer>> persistJob(@RequestBody JobPersistParam param) throws Exception {
		int jobId = jobManager.persistJob(param);
		return ResponseEntity.ok(JobResult.success(jobId));
	}

	@PostMapping("/finishJob")
	public ResponseEntity<JobResult<JobState>> finishJob(@RequestBody JobKey jobKey) throws Exception {
		JobState jobState = jobManager.finishJob(jobKey);
		return ResponseEntity.ok(JobResult.success(jobState));
	}

	@PostMapping("/hasJob")
	public ResponseEntity<JobResult<Boolean>> hasJob(@RequestBody JobKey jobKey) throws Exception {
		boolean has = jobManager.hasJob(jobKey);
		return ResponseEntity.ok(JobResult.success(has));
	}

	@PostMapping("/hasJobState")
	public ResponseEntity<JobResult<Boolean>> hasJobState(@RequestBody JobStateParam param) throws Exception {
		boolean has = jobManager.hasJobState(param.getJobKey(), param.getJobState());
		return ResponseEntity.ok(JobResult.success(has));
	}

	@PostMapping("/getJobId")
	public ResponseEntity<JobResult<Integer>> getJobId(@RequestBody JobKey jobKey) throws Exception {
		int jobId = jobManager.getJobId(jobKey);
		return ResponseEntity.ok(JobResult.success(jobId));
	}

	@PostMapping("/getJobDetail")
	public ResponseEntity<JobResult<JobDetail>> getJobDetail(@RequestBody JobKey jobKey) throws Exception {
		JobDetail jobDetail = jobManager.getJobDetail(jobKey, true);
		return ResponseEntity.ok(JobResult.success(jobDetail));
	}

	@PostMapping("/getJobTriggerDetail")
	public ResponseEntity<JobResult<JobTriggerDetail>> getJobTriggerDetail(@RequestBody JobKey jobKey) throws Exception {
		JobTriggerDetail jobTriggerDetail = jobManager.getJobTriggerDetail(jobKey);
		return ResponseEntity.ok(JobResult.success(jobTriggerDetail));
	}

	@PostMapping("/getJobRuntime")
	public ResponseEntity<JobResult<JobRuntime>> getJobRuntime(@RequestBody JobKey jobKey) throws Exception {
		JobRuntime jobRuntime = jobManager.getJobRuntime(jobKey);
		return ResponseEntity.ok(JobResult.success(jobRuntime));
	}

	@PostMapping("/hasRelations")
	public ResponseEntity<JobResult<Boolean>> hasRelations(@RequestBody JobDependencyParam param) throws Exception {
		boolean result = jobManager.hasRelations(param.getJobKey(), param.getDependencyType());
		return ResponseEntity.ok(JobResult.success(result));
	}

	@PostMapping("/getRelations")
	public ResponseEntity<JobResult<JobKey[]>> getRelations(@RequestBody JobDependencyParam param) throws Exception {
		JobKey[] jobKeys = jobManager.getRelations(param.getJobKey(), param.getDependencyType());
		return ResponseEntity.ok(JobResult.success(jobKeys));
	}

	@PostMapping("/getDependentKeys")
	public ResponseEntity<JobResult<JobKey[]>> getDependencies(@RequestBody JobDependencyParam param) throws Exception {
		JobKey[] jobKeys = jobManager.getDependentKeys(param.getJobKey(), param.getDependencyType());
		return ResponseEntity.ok(JobResult.success(jobKeys));
	}

	@PostMapping("/setJobState")
	public ResponseEntity<JobResult<JobState>> setJobState(@RequestBody JobStateParam param) throws Exception {
		JobState jobState = jobManager.setJobState(param.getJobKey(), param.getJobState());
		return ResponseEntity.ok(JobResult.success(jobState));
	}

	@PostMapping("/getJobKeys")
	public ResponseEntity<JobResult<JobKey[]>> getJobKeys(@RequestBody JobKeyQuery jobQuery) throws Exception {
		JobKey[] jobKeys = jobManager.getJobKeys(jobQuery);
		return ResponseEntity.ok(JobResult.success(jobKeys));
	}

	@PostMapping("/selectJobDetail")
	public ResponseEntity<JobResult<PageQuery<JobDetail>>> selectJobDetail(@RequestBody PageQuery<JobDetail> pageQuery) throws Exception {
		jobManager.selectJobDetail(pageQuery);
		return ResponseEntity.ok(JobResult.success(pageQuery));
	}

	@PostMapping("/selectJobTrace")
	public ResponseEntity<JobResult<PageQuery<JobTrace>>> selectJobTrace(@RequestBody JobTracePageQuery<JobTrace> pageQuery)
			throws Exception {
		jobManager.selectJobTrace(pageQuery);
		return ResponseEntity.ok(JobResult.success(pageQuery));
	}

	@PostMapping("/selectJobLog")
	public ResponseEntity<JobResult<JobLog[]>> selectJobLog(@RequestBody JobTraceQuery query) throws Exception {
		JobLog[] logs = jobManager.selectJobLog(query);
		return ResponseEntity.ok(JobResult.success(logs));
	}

	@PostMapping("/selectJobStackTrace")
	public ResponseEntity<JobResult<JobStackTrace[]>> selectJobStackTrace(@RequestBody JobTraceQuery query) throws Exception {
		JobStackTrace[] traces = jobManager.selectJobStackTrace(query);
		return ResponseEntity.ok(JobResult.success(traces));
	}

	@PostMapping("/onJobBegin")
	public ResponseEntity<JobResult<JobState>> onJobBegin(@RequestBody JobRuntimeParam param) throws Exception {
		JobState jobState = stopWatch.onJobBegin(param.getTraceId(), param.getJobKey(), param.getStartTime());
		return ResponseEntity.ok(JobResult.success(jobState));
	}

	@PostMapping("/onJobEnd")
	public ResponseEntity<JobResult<JobState>> onJobEnd(@RequestBody JobRuntimeParam param) throws Exception {
		JobState jobState = stopWatch.onJobEnd(param.getTraceId(), param.getJobKey(), param.getStartTime(), param.getRunningState(),
				param.getRetries());
		return ResponseEntity.ok(JobResult.success(jobState));
	}

	@PostMapping("/generateTraceId")
	public ResponseEntity<JobResult<Long>> generateTraceId(@RequestBody JobKey jobKey) {
		long traceId = traceIdGenerator.generateTraceId(jobKey);
		return ResponseEntity.ok(JobResult.success(traceId));
	}

	@PostMapping("/log")
	public ResponseEntity<JobResult<String>> log(@RequestBody JobLogParam param) throws Exception {
		logManager.log(param.getTraceId(), param.getJobKey(), param.getLogLevel(), param.getMessagePattern(), param.getArgs(),
				param.getStackTraces());
		return ResponseEntity.ok(JobResult.success("ok"));
	}

}