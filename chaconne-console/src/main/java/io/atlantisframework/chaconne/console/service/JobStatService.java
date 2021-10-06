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
package io.atlantisframework.chaconne.console.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.beans.BeanUtils;

import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.console.utils.JobStatForm;
import io.atlantisframework.chaconne.model.JobStat;
import io.atlantisframework.chaconne.model.JobStatDetail;
import io.atlantisframework.chaconne.model.JobStatPageQuery;
import io.atlantisframework.chaconne.model.JobStatQuery;
import io.atlantisframework.chaconne.model.PageQuery;

/**
 * 
 * JobStatService
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Component
public class JobStatService {

	@Autowired
	private JobManager jobManager;

	public PageQuery<JobStatDetail> selectJobStatById(String clusterName, JobStatForm form, int page, int size) throws Exception {
		JobStatPageQuery<JobStatDetail> query = new JobStatPageQuery<>();
		query.setClusterName(clusterName);
		if (form != null) {
			BeanUtils.copyProperties(form, query);
		}
		query.setPage(page);
		query.setSize(size);
		jobManager.selectJobStatById(query);
		return query;
	}

	public JobStatDetail[] selectJobStatByDay(String clusterName, JobStatForm form) throws Exception {
		JobStatQuery query = new JobStatQuery(clusterName);
		BeanUtils.copyProperties(form, query);
		return jobManager.selectJobStatByDay(query);
	}

	public JobStatDetail[] selectJobStatByMonth(String clusterName, JobStatForm form) throws Exception {
		JobStatQuery query = new JobStatQuery(clusterName);
		BeanUtils.copyProperties(form, query);
		return jobManager.selectJobStatByMonth(query);
	}

	public JobStat selectJobStatById(String clusterName, int jobId) throws Exception {
		return jobManager.selectJobStat(new JobStatQuery(clusterName, jobId));
	}

}
