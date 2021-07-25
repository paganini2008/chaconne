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
package indi.atlantis.framework.chaconne;

/**
 * 
 * IllegalJobStateException
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class IllegalJobStateException extends JobException {

	private static final long serialVersionUID = 2500171134294863349L;

	public IllegalJobStateException(JobKey jobKey) {
		super();
		this.jobKey = jobKey;
	}

	public IllegalJobStateException(JobKey jobKey, String msg) {
		super(msg);
		this.jobKey = jobKey;
	}

	private final JobKey jobKey;

	public JobKey getJobKey() {
		return jobKey;
	}

}
