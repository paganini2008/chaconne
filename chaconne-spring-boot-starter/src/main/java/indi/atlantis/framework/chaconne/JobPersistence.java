package indi.atlantis.framework.chaconne;

import indi.atlantis.framework.chaconne.dag.DagDefination;
import indi.atlantis.framework.chaconne.model.JobPersistParam;
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

	default int persistJob(JobPersistParam param) throws Exception {
		JobDefinition jobDefinition = GenericJobDefinition.parse(param).build();
		return persistJob(jobDefinition, param.getAttachment());
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
