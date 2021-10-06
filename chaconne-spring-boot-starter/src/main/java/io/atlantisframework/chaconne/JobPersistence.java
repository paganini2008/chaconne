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
package io.atlantisframework.chaconne;

import io.atlantisframework.chaconne.dag.DagDefination;
import io.atlantisframework.chaconne.model.JobPersistParameter;
import io.atlantisframework.chaconne.utils.GenericJobDefinition;

/**
 * 
 * JobPersistence
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public interface JobPersistence {

	default int persistJob(JobPersistParameter parameter) throws Exception {
		JobDefinition jobDefinition = GenericJobDefinition.parse(parameter).build();
		return persistJob(jobDefinition, parameter.getAttachment());
	}

	default int persistJob(DagDefination dagDefination, String attachment) throws Exception {
		JobDefinition[] jobDefinitions = dagDefination.getJobDefinitions();
		int id = persistJob(jobDefinitions[0], attachment);
		for (int i = 1; i < jobDefinitions.length; i++) {
			persistJob(jobDefinitions[i], null);
		}
		return id;
	}

	default int persistJob(JobDefinition jobDefinition, String attachment) throws Exception {
		throw new UnsupportedOperationException("persistJob");
	}

	default JobState finishJob(JobKey jobKey) throws Exception {
		throw new UnsupportedOperationException("finishJob");
	}

	default boolean hasJob(JobKey jobKey) throws Exception {
		return true;
	}

	JobState pauseJob(JobKey jobKey) throws Exception;

	JobState resumeJob(JobKey jobKey) throws Exception;

	boolean hasJobState(JobKey jobKey, JobState jobState) throws Exception;

	JobState setJobState(JobKey jobKey, JobState jobState) throws Exception;

}
