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
package indi.atlantis.framework.chaconne.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * JobStatQuery
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Getter
@Setter
public class JobStatQuery extends Query {

	public JobStatQuery() {
	}

	public JobStatQuery(String clusterName) {
		super(clusterName);
	}

	public JobStatQuery(String clusterName, Integer jobId) {
		super(clusterName);
		this.jobId = jobId;
	}

	private String applicationName;
	private Integer jobId;
	private String address;
	private int lastDays = 30;

}
