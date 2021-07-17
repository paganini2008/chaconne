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
package indi.atlantis.framework.chaconne.console.controller;

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

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.chaconne.JacksonUtils;
import indi.atlantis.framework.chaconne.console.service.JobManagerService;
import indi.atlantis.framework.chaconne.console.utils.JobLogForm;
import indi.atlantis.framework.chaconne.console.utils.JobTraceForm;
import indi.atlantis.framework.chaconne.console.utils.PageBean;
import indi.atlantis.framework.chaconne.console.utils.Result;
import indi.atlantis.framework.chaconne.model.JobDetail;
import indi.atlantis.framework.chaconne.model.JobLog;
import indi.atlantis.framework.chaconne.model.JobPersistParameter;
import indi.atlantis.framework.chaconne.model.JobStackTrace;
import indi.atlantis.framework.chaconne.model.JobTrace;
import indi.atlantis.framework.chaconne.model.PageQuery;

/**
 * 
 * JobManagerController
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@RequestMapping("/job")
@Controller
public class JobManagerController {

	@Autowired
	private JobManagerService jobManagerService;

	@GetMapping("")
	public String index(Model ui) {
		return "job_man";
	}

	@GetMapping("/clusters")
	public @ResponseBody Result<String[]> selectRegisteredClusterNames() throws Exception {
		String[] clusterNames = jobManagerService.selectRegisteredClusterNames();
		return Result.success(clusterNames);
	}

	@PostMapping("/save")
	public @ResponseBody Result<Map<String, Object>> saveJob(@RequestBody JobPersistParameter param) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			int id = jobManagerService.saveJob(param);
			data.put("success", id > 0);
		} catch (Exception e) {
			data.put("success", false);
			data.put("errorMsg", e.getMessage());
		}
		return Result.success(data);
	}

	@GetMapping("/toggle/{jobKey}")
	public String toggleJob(@PathVariable("jobKey") String jobKey) throws Exception {
		jobManagerService.toggleJob(jobKey);
		return "redirect:/job";
	}

	@GetMapping(value = { "/edit", "/edit/{jobKey}" })
	public String editJob(@PathVariable(name = "jobKey", required = false) String jobKey, Model ui) throws Exception {
		JobPersistParameter param;
		if (StringUtils.isNotBlank(jobKey)) {
			JobDetail jobDetail = jobManagerService.getJobDetail(jobKey);
			param = JobPersistParameter.wrap(jobDetail);
		} else {
			param = JobPersistParameter.forExample();
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
		return "job_man_list";
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
