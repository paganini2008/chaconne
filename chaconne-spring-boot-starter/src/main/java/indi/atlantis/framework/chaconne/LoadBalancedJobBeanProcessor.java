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
package indi.atlantis.framework.chaconne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import indi.atlantis.framework.chaconne.model.JobParameter;
import indi.atlantis.framework.tridenter.ApplicationInfo;
import indi.atlantis.framework.tridenter.Constants;
import indi.atlantis.framework.tridenter.multicast.ApplicationMessageListener;

/**
 * 
 * LoadBalancedJobBeanProcessor
 *
 * @author Fred Feng
 * @since 1.0
 */
public class LoadBalancedJobBeanProcessor implements ApplicationMessageListener {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Qualifier(ChaconneBeanNames.TARGET_JOB_EXECUTOR)
	@Autowired
	private JobExecutor jobExecutor;

	@Qualifier(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
	@Autowired
	private JobBeanLoader jobBeanLoader;

	@Override
	public void onMessage(ApplicationInfo applicationInfo, String id, Object message) {
		JobParameter jobParam = (JobParameter) message;
		Job job;
		try {
			job = jobBeanLoader.loadJobBean(jobParam.getJobKey());
		} catch (Exception e) {
			throw ExceptionUtils.wrapExeception(e);
		}
		jobExecutor.execute(job, jobParam.getAttachment(), jobParam.getRetries());
	}

	@Override
	public String getTopic() {
		return Constants.APPLICATION_CLUSTER_NAMESPACE + clusterName + ":scheduler:loadbalance";
	}

}
