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

import org.slf4j.Logger;
import org.slf4j.Marker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.paganini2008.devtools.enums.EnumConstant;

/**
 * 
 * LogLevel
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public enum LogLevel implements EnumConstant {

	TRACE(0, "trace") {
		@Override
		public boolean canLog(Logger log) {
			return log.isTraceEnabled();
		}

		@Override
		public boolean canLog(Logger log, Marker marker) {
			return log.isTraceEnabled(marker);
		}

	},
	DEBUG(1, "debug") {
		@Override
		public boolean canLog(Logger log) {
			return log.isDebugEnabled();
		}

		@Override
		public boolean canLog(Logger log, Marker marker) {
			return log.isDebugEnabled(marker);
		}
	},
	INFO(2, "info") {
		@Override
		public boolean canLog(Logger log) {
			return log.isInfoEnabled();
		}

		@Override
		public boolean canLog(Logger log, Marker marker) {
			return log.isInfoEnabled(marker);
		}
	},
	WARN(3, "warn") {
		@Override
		public boolean canLog(Logger log) {
			return log.isWarnEnabled();
		}

		@Override
		public boolean canLog(Logger log, Marker marker) {
			return log.isWarnEnabled(marker);
		}
	},
	ERROR(4, "error") {
		@Override
		public boolean canLog(Logger log) {
			return log.isErrorEnabled();
		}

		@Override
		public boolean canLog(Logger log, Marker marker) {
			return log.isErrorEnabled(marker);
		}

	};

	public abstract boolean canLog(Logger log);

	public abstract boolean canLog(Logger log, Marker marker);

	private final int value;
	private final String repr;

	private LogLevel(int value, String repr) {
		this.value = value;
		this.repr = repr;
	}

	@JsonValue
	public int getValue() {
		return value;
	}

	@Override
	public String getRepr() {
		return repr;
	}

	@JsonCreator
	public static LogLevel valueOf(int value) {
		for (LogLevel logLevel : LogLevel.values()) {
			if (logLevel.getValue() == value) {
				return logLevel;
			}
		}
		throw new IllegalArgumentException("Unknown logLevel: " + value);
	}

}
