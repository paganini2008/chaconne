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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import com.github.paganini2008.devtools.StringUtils;

import indi.atlantis.framework.chaconne.console.service.JobManagerService;

/**
 * 
 * IndexController
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Controller
public class IndexController {

	@Autowired
	private JobManagerService jobManagerService;

	@GetMapping("/index")
	public String index() {
		return "forward:/index/1";
	}

	@GetMapping("/index/99")
	public String underConstruction() {
		return "under_construction";
	}

	@GetMapping("/index/{index}")
	public String index(@PathVariable("index") int index, @RequestParam(name = "clusterName", required = false) String clusterName,
			WebRequest webRequest, Model ui) throws Exception {
		String currentClusterName = clusterName;
		if (StringUtils.isBlank(currentClusterName)) {
			String[] clusterNames = jobManagerService.selectRegisteredClusterNames();
			currentClusterName = clusterNames[0];
		}
		webRequest.setAttribute("currentClusterName", currentClusterName, RequestAttributes.SCOPE_SESSION);
		ui.addAttribute("navIndex", index);
		switch (index) {
		case 1:
			return "redirect:/overview";
		case 2:
			return "redirect:/job";
		case 3:
			return "redirect:/job/stat";
		default:
			return "redirect:/index/99";
		}
	}

}
