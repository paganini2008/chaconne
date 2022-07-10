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

/**
 * 
 * StatType
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public enum StatType {

	BY_YEAR("DATE_FORMAT(execute_time,'%Y') AS execution", "execution"), BY_MONTH("DATE_FORMAT(execute_time,'%Y-%m') AS execution",
			"execution"), BY_DATE("DATE_FORMAT(execute_time,'%Y-%m-%d') AS execution", "execution");

	private final String extraColumns;
	private final String extraGroupingColumns;

	private StatType(String extraColumns, String extraGroupingColumns) {
		this.extraColumns = extraColumns;
		this.extraGroupingColumns = extraGroupingColumns;
	}

	public String getExtraColumns() {
		return extraColumns;
	}

	public String getExtraGroupingColumns() {
		return extraGroupingColumns;
	}

}
