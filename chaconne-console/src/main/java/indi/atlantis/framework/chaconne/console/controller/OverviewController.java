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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import indi.atlantis.framework.chaconne.console.service.OverviewService;
import indi.atlantis.framework.chaconne.console.utils.Result;
import indi.atlantis.framework.chaconne.model.JobStat;
import indi.atlantis.framework.chaconne.model.JobStatDetail;
import indi.atlantis.framework.chaconne.model.JobStateCount;

/**
 * 
 * OverviewController
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@RequestMapping("/overview")
@Controller
public class OverviewController {

	@Autowired
	private OverviewService overviewService;

	@GetMapping("")
	public String index(Model ui) {
		return "overview";
	}

	@PostMapping("/job/state")
	public @ResponseBody Result<JobStateCount[]> selectJobStateCount(@SessionAttribute("currentClusterName") String clusterName)
			throws Exception {
		JobStateCount[] stateCounts = overviewService.selectJobStateCount(clusterName);
		return Result.success(stateCounts);
	}

	@PostMapping("/job/stat")
	public @ResponseBody Result<JobStat> selectJobStat(@SessionAttribute("currentClusterName") String clusterName) throws Exception {
		JobStat jobStat = overviewService.selectJobStat(clusterName);
		return Result.success(jobStat);
	}

	@PostMapping("/job/stat/detail")
	public @ResponseBody Result<JobStatDetail[]> selectJobStatByDay(@SessionAttribute("currentClusterName") String clusterName)
			throws Exception {
		JobStatDetail[] jobStats = overviewService.selectJobStatByDay(clusterName);
		return Result.success(jobStats);
	}

}
