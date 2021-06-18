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

import indi.atlantis.framework.chaconne.dag.DagDefination;
import indi.atlantis.framework.chaconne.model.JobPersistParameter;
import indi.atlantis.framework.chaconne.utils.GenericJobDefinition;

/**
 * 
 * JobPersistence
 * 
 * @author Fred Feng
 *
 * @version 1.0
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
