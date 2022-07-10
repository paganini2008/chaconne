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

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.paganini2008.devtools.enums.EnumConstant;

/**
 * 
 * SchedulingUnit
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public enum SchedulingUnit implements EnumConstant {

	SECONDS(0, "Per Second", TimeUnit.SECONDS), MINUTES(1, "Per Minute", TimeUnit.MINUTES), HOURS(2, "Per Hour", TimeUnit.HOURS), DAYS(3,
			"Per Day", TimeUnit.DAYS);

	private final int value;
	private final String repr;
	private final TimeUnit timeUnit;

	private SchedulingUnit(int value, String repr, TimeUnit timeUnit) {
		this.value = value;
		this.timeUnit = timeUnit;
		this.repr = repr;
	}

	@Override
	@JsonValue
	public int getValue() {
		return value;
	}

	@Override
	public String getRepr() {
		return repr;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	@JsonCreator
	public static SchedulingUnit valueOf(int value) {
		for (SchedulingUnit unit : SchedulingUnit.values()) {
			if (unit.getValue() == value) {
				return unit;
			}
		}
		throw new IllegalArgumentException("Unknown SchedulingUnit: " + value);
	}

}
