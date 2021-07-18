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
package indi.atlantis.framework.chaconne.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import indi.atlantis.framework.chaconne.JacksonUtils;
import indi.atlantis.framework.chaconne.TriggerType;
import lombok.Getter;

/**
 * 
 * JobTriggerDetail
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@JsonInclude(value = Include.NON_NULL)
@Getter
public class JobTriggerDetail implements Serializable {

	private static final long serialVersionUID = 866085363330905946L;
	private int jobId;
	private TriggerType triggerType;
	private Date startDate;
	private Date endDate;
	private int repeatCount;
	private String triggerDescription;

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public void setTriggerType(int triggerType) {
		this.triggerType = TriggerType.valueOf(triggerType);
	}

	public void setTriggerDescription(String triggerDescription) {
		this.triggerDescription = triggerDescription;
	}

	public String getTriggerDescription() {
		return triggerDescription;
	}

	@JsonIgnore
	public TriggerDescription getTriggerDescriptionObject() {
		return JacksonUtils.parseJson(triggerDescription, TriggerDescription.class);
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

}
