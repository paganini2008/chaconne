/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package io.atlantisframework.chaconne.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * JobStatDetail
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Getter
@Setter
@ToString
@JsonInclude(value = Include.NON_NULL)
public class JobStatDetail implements Serializable {

	private static final long serialVersionUID = 5741263651318840914L;

	private Integer jobId;
	private String clusterName;
	private String groupName;
	private String jobName;
	private String jobClassName;
	private int completedCount;
	private int skippedCount;
	private int failedCount;
	private int finishedCount;
	private int retryCount;
	private String executionDate;

	public JobStatDetail() {
	}

	public JobStatDetail(String executionDate) {
		this.executionDate = executionDate;
	}

	public JobStatDetail(String clusterName, String executionDate) {
		this.clusterName = clusterName;
		this.executionDate = executionDate;
	}

}
