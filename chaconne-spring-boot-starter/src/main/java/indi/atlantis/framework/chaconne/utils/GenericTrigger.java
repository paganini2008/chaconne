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
package indi.atlantis.framework.chaconne.utils;

import java.util.Date;

import indi.atlantis.framework.chaconne.SchedulingUnit;
import indi.atlantis.framework.chaconne.Trigger;
import indi.atlantis.framework.chaconne.TriggerType;
import indi.atlantis.framework.chaconne.model.JobTriggerParameter;
import indi.atlantis.framework.chaconne.model.TriggerDescription;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * GenericTrigger
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Getter
public class GenericTrigger implements Trigger {

	private final TriggerType triggerType;
	private final TriggerDescription triggerDescription;
	private final Date startDate;
	private final Date endDate;
	private final int repeatCount;

	GenericTrigger(Builder builder) {
		this.triggerType = builder.triggerType;
		this.triggerDescription = builder.triggerDescription;
		this.startDate = builder.startDate;
		this.endDate = builder.endDate;
		this.repeatCount = builder.repeatCount;
	}

	@Accessors(chain = true)
	@Getter
	@Setter
	public static class Builder {

		private TriggerDescription triggerDescription;
		private TriggerType triggerType;
		private Date startDate;
		private Date endDate;
		private int repeatCount = -1;

		Builder() {
			this.triggerDescription = new TriggerDescription();
			this.triggerType = TriggerType.SIMPLE;
		}

		Builder(String cronExpression) {
			this.triggerDescription = new TriggerDescription(cronExpression);
			this.triggerType = TriggerType.CRON;
		}

		Builder(long period, SchedulingUnit schedulingUnit, boolean fixedRate) {
			this.triggerDescription = new TriggerDescription(period, schedulingUnit, fixedRate);
			this.triggerType = TriggerType.PERIODIC;
		}

		public static Builder newTrigger() {
			return new Builder();
		}

		public static Builder newTrigger(String cronExpression) {
			return new Builder(cronExpression);
		}

		public static Builder newTrigger(long period, SchedulingUnit schedulingUnit, boolean fixedRate) {
			return new Builder(period, schedulingUnit, fixedRate);
		}

		public Trigger build() {
			return new GenericTrigger(this);
		}

	}

	public static Builder parse(JobTriggerParameter triggerParam) {
		return GenericTrigger.Builder.newTrigger().setTriggerType(triggerParam.getTriggerType())
				.setTriggerDescription(triggerParam.getTriggerDescription()).setStartDate(triggerParam.getStartDate())
				.setEndDate(triggerParam.getEndDate()).setRepeatCount(triggerParam.getRepeatCount());
	}

}
