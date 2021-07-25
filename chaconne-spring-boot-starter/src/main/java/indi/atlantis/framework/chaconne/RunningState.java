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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.paganini2008.devtools.enums.EnumConstant;

/**
 * 
 * RunningState
 *
 * @author Fred Feng
 * @since 1.0
 */
public enum RunningState implements EnumConstant {

	FAILED(0, "Failed"), COMPLETED(1, "Completed"), SKIPPED(2, "Skipped"), FINISHED(3, "Finished"), RUNNING(99, "Running");

	private final int value;
	private final String repr;

	private RunningState(int value, String repr) {
		this.value = value;
		this.repr = repr;
	}

	@JsonValue
	public int getValue() {
		return value;
	}

	public String getRepr() {
		return repr;
	}

	@JsonCreator
	public static RunningState valueOf(int value) {
		for (RunningState state : RunningState.values()) {
			if (state.getValue() == value) {
				return state;
			}
		}
		throw new IllegalArgumentException("Unknown value: " + value);
	}

}
