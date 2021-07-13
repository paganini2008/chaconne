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
package indi.atlantis.framework.chaconne.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobStat
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
@Setter
public class JobStat implements Serializable {

	private static final long serialVersionUID = 5741263651318840914L;

	private String clusterName;
	private String groupName;
	private Integer jobId;
	private String jobName;
	private int completedCount;
	private int skippedCount;
	private int failedCount;
	private int finishedCount;
	private int retryCount;
	private String executionDate;

	public JobStat() {
	}

	public JobStat(String executionDate) {
		this.executionDate = executionDate;
	}

	public JobStat(String clusterName, String executionDate) {
		this.clusterName = clusterName;
		this.executionDate = executionDate;
	}

}
