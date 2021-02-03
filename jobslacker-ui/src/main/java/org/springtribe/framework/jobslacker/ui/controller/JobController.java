package org.springtribe.framework.jobslacker.ui.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springtribe.framework.jobslacker.JacksonUtils;
import org.springtribe.framework.jobslacker.model.JobDetail;
import org.springtribe.framework.jobslacker.model.JobLog;
import org.springtribe.framework.jobslacker.model.JobPersistParam;
import org.springtribe.framework.jobslacker.model.JobStackTrace;
import org.springtribe.framework.jobslacker.model.JobTrace;
import org.springtribe.framework.jobslacker.model.PageQuery;
import org.springtribe.framework.jobslacker.ui.JobLogForm;
import org.springtribe.framework.jobslacker.ui.JobTraceForm;
import org.springtribe.framework.jobslacker.ui.service.JobManagerService;
import org.springtribe.framework.jobslacker.ui.utils.PageBean;

import com.github.paganini2008.devtools.StringUtils;

/**
 * 
 * JobController
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@RequestMapping("/job")
@Controller
public class JobController {

	@Autowired
	private JobManagerService jobManagerService;

	@PostMapping("/save")
	public @ResponseBody Map<String, Object> saveJob(@RequestBody JobPersistParam param) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			jobManagerService.saveJob(param);
			data.put("success", true);
		} catch (Exception e) {
			data.put("success", false);
			data.put("msg", "Server Internal Error: " + e.getMessage());
		}
		return data;
	}

	@GetMapping(value = { "/edit", "/edit/{jobKey}" })
	public String editJob(@PathVariable(name = "jobKey", required = false) String jobKey, Model ui) throws Exception {
		JobPersistParam param;
		if (StringUtils.isNotBlank(jobKey)) {
			JobDetail jobDetail = jobManagerService.getJobDetail(jobKey);
			param = JobPersistParam.wrap(jobDetail);
		} else {
			param = JobPersistParam.forExample();
		}
		ui.addAttribute("jobDefinition", JacksonUtils.toJsonString(param));
		return "job_edit";
	}

	@PostMapping("")
	public String selectJobDetail(@SessionAttribute("currentClusterName") String clusterName,
			@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "DATA_LIST_SIZE", required = false, defaultValue = "10") int size, Model ui) throws Exception {
		PageQuery<JobDetail> pageQuery = jobManagerService.selectJobDetail(clusterName, page, size);
		ui.addAttribute("page", PageBean.wrap(pageQuery));
		return "job_list";
	}

	@PostMapping("/trace")
	public String selectJobTrace(@ModelAttribute JobTraceForm form,
			@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "DATA_LIST_SIZE", required = false, defaultValue = "10") int size, Model ui) throws Exception {
		PageQuery<JobTrace> pageQuery = jobManagerService.selectJobTrace(form, page, size);
		ui.addAttribute("page", PageBean.wrap(pageQuery));
		ui.addAttribute("jobKey", form.getJobKey());
		return "job_trace";
	}

	@GetMapping("/detail/{jobKey}")
	public String selectJobDetail(@PathVariable("jobKey") String jobKey, Model ui) throws Exception {
		JobDetail jobDetail = jobManagerService.getJobDetail(jobKey);
		ui.addAttribute("jobDetail", jobDetail);
		return "job_detail";
	}

	@GetMapping("/log/{jobKey}")
	public String selectJobLog(@PathVariable("jobKey") String jobKey, JobLogForm form, Model ui) throws Exception {
		form.setJobKey(jobKey);
		JobLog[] logs = jobManagerService.selectJobLog(form);
		ui.addAttribute("logs", logs);
		ui.addAttribute("jobKey", jobKey);
		return "job_log";
	}

	@GetMapping("/error/{jobKey}")
	public String selectJobStackTrace(@PathVariable("jobKey") String jobKey, @ModelAttribute JobLogForm form, Model ui) throws Exception {
		form.setJobKey(jobKey);
		JobStackTrace[] stackTraceArray = jobManagerService.selectJobStackTrace(form);
		ui.addAttribute("stackTraceArray", stackTraceArray);
		return "job_error";
	}

}
