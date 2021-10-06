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
package io.atlantisframework.chaconne;

import java.util.Date;

import io.atlantisframework.chaconne.model.TriggerDescription;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * PeriodicTrigger
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Accessors(chain = true)
@Getter
@Setter
public class PeriodicTrigger implements Trigger {

	private Date startDate;
	private Date endDate;
	private int repeatCount = -1;
	private final TriggerDescription triggerDescription;

	public PeriodicTrigger(int period, SchedulingUnit schedulingUnit, boolean fixedRate) {
		this.triggerDescription = new TriggerDescription(period, schedulingUnit, fixedRate);
	}

	public PeriodicTrigger(TriggerDescription triggerDescription) {
		this.triggerDescription = triggerDescription;
	}

	@Override
	public TriggerType getTriggerType() {
		return TriggerType.PERIODIC;
	}

}