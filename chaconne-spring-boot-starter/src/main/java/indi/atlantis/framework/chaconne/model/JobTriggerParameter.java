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

import java.util.Date;

import indi.atlantis.framework.chaconne.SchedulingUnit;
import indi.atlantis.framework.chaconne.TriggerType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * JobTriggerParameter
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Getter
@Setter
@ToString
public class JobTriggerParameter {

	private TriggerType triggerType;
	private TriggerDescription triggerDescription;
	private Date startDate;
	private Date endDate;
	private int repeatCount = -1;

	public JobTriggerParameter() {
		this.triggerDescription = new TriggerDescription();
		this.triggerType = TriggerType.SIMPLE;
	}

	public JobTriggerParameter(String cronExpression) {
		this.triggerDescription = new TriggerDescription(cronExpression);
		this.triggerType = TriggerType.CRON;
	}

	public JobTriggerParameter(long period, SchedulingUnit schedulingUnit, boolean fixedRate) {
		this.triggerDescription = new TriggerDescription(period, schedulingUnit, fixedRate);
		this.triggerType = TriggerType.PERIODIC;
	}

}
