/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import org.slf4j.Logger;

import com.github.paganini2008.devtools.reflection.MethodUtils;

import indi.atlantis.framework.chaconne.annotations.OnFailure;
import indi.atlantis.framework.chaconne.annotations.OnSuccess;
import indi.atlantis.framework.chaconne.annotations.Run;

/**
 * 
 * AnnotatedJobBeanProxy
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class AnnotatedJobBeanProxy implements NotManagedJob {

	private final Object targetBean;

	AnnotatedJobBeanProxy(Object targetBean) {
		this.targetBean = targetBean;
	}

	@Override
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		return MethodUtils.invokeMethodWithAnnotation(targetBean, Run.class, jobKey, attachment, log);
	}

	@Override
	public void onSuccess(JobKey jobKey, Object result, Logger log) {
		MethodUtils.invokeMethodsWithAnnotation(targetBean, OnSuccess.class, jobKey, result, log);
	}

	@Override
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		MethodUtils.invokeMethodsWithAnnotation(targetBean, OnFailure.class, jobKey, e, log);
	}

}
