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
package indi.atlantis.framework.chaconne.console.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import indi.atlantis.framework.chaconne.console.service.JobStatService;
import indi.atlantis.framework.chaconne.console.utils.JobStatForm;
import indi.atlantis.framework.chaconne.console.utils.PageBean;
import indi.atlantis.framework.chaconne.console.utils.Result;
import indi.atlantis.framework.chaconne.model.JobStatDetail;
import indi.atlantis.framework.chaconne.model.PageQuery;

/**
 * 
 * JobStatController
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@RequestMapping("/job/stat")
@Controller
public class JobStatController {

	@Autowired
	private JobStatService jobStatService;

	@GetMapping("")
	public String index(Model ui) throws Exception {
		return "job_stat";
	}

	@PostMapping("/list")
	public String selectJobStatById(@SessionAttribute("currentClusterName") String clusterName,
			@RequestParam(value = "page", defaultValue = "1", required = false) int page,
			@CookieValue(value = "DATA_LIST_SIZE", required = false, defaultValue = "10") int size, @ModelAttribute JobStatForm form,
			Model ui) throws Exception {
		PageQuery<JobStatDetail> pageQuery = jobStatService.selectJobStatById(clusterName, form, page, size);
		ui.addAttribute("page", PageBean.wrap(pageQuery));
		return "job_stat_list";
	}

	@PostMapping("/detail/day")
	public @ResponseBody Result<JobStatDetail[]> selectJobStatByDay(@SessionAttribute("currentClusterName") String clusterName,
			@RequestBody JobStatForm form) throws Exception {
		JobStatDetail[] jobStats = jobStatService.selectJobStatByDay(clusterName, form);
		return Result.success(jobStats);
	}

	@PostMapping("/detail/month")
	public @ResponseBody Result<JobStatDetail[]> selectJobStatById(@SessionAttribute("currentClusterName") String clusterName,
			@RequestBody JobStatForm form) throws Exception {
		JobStatDetail[] jobStats = jobStatService.selectJobStatByMonth(clusterName, form);
		return Result.success(jobStats);
	}

}
