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
package io.atlantisframework.chaconne;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.paganini2008.devtools.enums.EnumConstant;
import com.github.paganini2008.devtools.enums.EnumUtils;

/**
 * 
 * JobState
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public enum JobState implements EnumConstant {

	NOT_SCHEDULED(0, "Not Scheduled"), SCHEDULING(1, "Scheduling"), RUNNING(2, "Running"), PAUSED(3, "Paused"), FINISHED(4,
			"Finished"), FROZEN(10, "Frozen"), NONE(99, "None");

	private JobState(int value, String repr) {
		this.value = value;
		this.repr = repr;
	}

	private final int value;
	private final String repr;

	@JsonValue
	public int getValue() {
		return value;
	}

	@Override
	public String getRepr() {
		return repr;
	}

	@JsonCreator
	public static JobState valueOf(Integer ordinal) {
		return EnumUtils.valueOf(JobState.class, ordinal);
	}

}
