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
package indi.atlantis.framework.chaconne.cluster;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import indi.atlantis.framework.chaconne.JobDao;
import indi.atlantis.framework.chaconne.JobQueryDao;
import indi.atlantis.framework.tridenter.ApplicationInfo;
import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;
import indi.atlantis.framework.tridenter.utils.Contact;
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
		if (jobQueryDao.selectJobServerExists(applicationInfo.getClusterName(), applicationInfo.getApplicationName(),
				applicationInfo.getApplicationContextPath()) == 0) {
			jobDao.saveJobServerDetail(kwargs);
			log.info("Registered job executor: {}", kwargs);
		}
	}

	public void unregisterJobExecutor(String clusterName, String groupName, String address) {
		Map<String, Object> kwargs = new HashMap<String, Object>();
		kwargs.put("clusterName", clusterName);
		kwargs.put("groupName", groupName);
		kwargs.put("contextPath", address);
		jobDao.deleteJobServerDetail(kwargs);
		log.info("Unregistered job executor: {}", kwargs);
	}

	public String[] getClusterContextPaths(String clusterName) {
		List<String> results = jobQueryDao.selectContextPath(clusterName);
		return results.toArray(new String[0]);
	}

}
