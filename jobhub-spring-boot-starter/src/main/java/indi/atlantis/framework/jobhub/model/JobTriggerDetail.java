package indi.atlantis.framework.jobhub.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import indi.atlantis.framework.jobhub.JacksonUtils;
import indi.atlantis.framework.jobhub.TriggerType;
import lombok.Getter;

/**
 * 
 * JobTriggerDetail
 * 
 * @author Jimmy Hoff
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
