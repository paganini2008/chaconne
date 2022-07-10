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

/**
 * 
 * DependencyType
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public enum DependencyType implements EnumConstant {

	SERIAL(0, "Serial"), PARALLEL(1, "Parallel"), MIXED(2, "Mixed");

	private final int value;
	private final String repr;

	private DependencyType(int value, String repr) {
		this.value = value;
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

	@JsonCreator
	public static DependencyType valueOf(int value) {
		for (DependencyType type : DependencyType.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unknown DependencyType: " + value);
	}

}
