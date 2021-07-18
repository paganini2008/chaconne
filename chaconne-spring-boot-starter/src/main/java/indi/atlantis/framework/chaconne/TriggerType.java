/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import indi.atlantis.framework.chaconne.model.TriggerDescription;
import indi.atlantis.framework.chaconne.model.TriggerDescription.Cron;
import indi.atlantis.framework.chaconne.model.TriggerDescription.Dependency;
import indi.atlantis.framework.chaconne.model.TriggerDescription.Periodic;

/**
 * 
 * TriggerType
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public enum TriggerType implements EnumConstant {

	SIMPLE(0, "Simple") {

		@Override
		public TriggerDescription getTriggerDescription() {
			return new TriggerDescription();
		}
	},

	CRON(1, "Cron") {

		@Override
		public TriggerDescription getTriggerDescription() {
			TriggerDescription triggerDescription = new TriggerDescription();
			triggerDescription.setCron(new Cron());
			return triggerDescription;
		}
	},

	PERIODIC(2, "Periodic") {

		@Override
		public TriggerDescription getTriggerDescription() {
			TriggerDescription triggerDescription = new TriggerDescription();
			triggerDescription.setPeriodic(new Periodic());
			return triggerDescription;
		}
	},

	DEPENDENT(3, "Dependent") {
		@Override
		public TriggerDescription getTriggerDescription() {
			TriggerDescription triggerDescription = new TriggerDescription();
			triggerDescription.setDependency(new Dependency());
			return triggerDescription;
		}
	};

	private final int value;
	private final String repr;

	private TriggerType(int value, String repr) {
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

	public abstract TriggerDescription getTriggerDescription();

	@JsonCreator
	public static TriggerType valueOf(int value) {
		for (TriggerType jobType : TriggerType.values()) {
			if (jobType.getValue() == value) {
				return jobType;
			}
		}
		throw new IllegalArgumentException("Unknown job type: " + value);
	}

}
