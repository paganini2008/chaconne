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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.atlantisframework.chaconne.JobDao;
import io.atlantisframework.chaconne.JobQueryDao;
import io.atlantisframework.tridenter.ApplicationInfo;
import io.atlantisframework.tridenter.utils.BeanLifeCycle;
import io.atlantisframework.tridenter.utils.Contact;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JobServerRegistry
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class JobServerRegistry implements BeanLifeCycle {

	@Autowired
	private JobDao jobDao;

	@Autowired
	private JobQueryDao jobQueryDao;

	@Override
	public void configure() throws Exception {
		jobDao.cleanJobServerDetail();
		log.info("Clean up cluster registry ok.");
	}

	public void registerJobExecutor(ApplicationInfo applicationInfo) {
		Map<String, Object> kwargs = new HashMap<String, Object>();
		kwargs.put("clusterName", applicationInfo.getClusterName());
		kwargs.put("groupName", applicationInfo.getApplicationName());
		kwargs.put("instanceId", applicationInfo.getId());
		kwargs.put("leader", applicationInfo.isLeader() ? 1 : 0);
		kwargs.put("contextPath", applicationInfo.getApplicationContextPath());
		kwargs.put("startDate", new Timestamp(applicationInfo.getStartTime()));
		Contact contact = applicationInfo.getContact();
		kwargs.put("contactPerson", contact.getName());
		kwargs.put("contactEmail", contact.getEmail());
		if (selectJobServerExists(applicationInfo.getClusterName(), applicationInfo.getApplicationName(),
				applicationInfo.getApplicationContextPath())) {
			jobDao.updateJobServerDetail(kwargs);
			log.info("Update registered job executor: {}", kwargs);
		} else {
			jobDao.saveJobServerDetail(kwargs);
			log.info("Register new job executor: {}", kwargs);
		}
	}

	public void unregisterJobExecutor(String clusterName, String groupName, String contextPath) {
		Map<String, Object> kwargs = new HashMap<String, Object>();
		kwargs.put("clusterName", clusterName);
		kwargs.put("groupName", groupName);
		kwargs.put("contextPath", contextPath);
		jobDao.deleteJobServerDetail(kwargs);
		log.info("Unregistered job executor: {}", kwargs);
	}

	public String[] getClusterContextPaths(String clusterName) {
		List<String> results = jobQueryDao.selectContextPath(clusterName);
		return results.toArray(new String[0]);
	}

	public boolean selectJobServerExists(String clusterName, String groupName, String contextPath) {
		return jobQueryDao.selectJobServerExists(clusterName, groupName, contextPath) > 0;
	}

}
