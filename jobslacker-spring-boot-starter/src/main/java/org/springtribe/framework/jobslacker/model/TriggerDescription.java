package org.springtribe.framework.jobslacker.model;

import java.io.Serializable;

import org.springframework.lang.Nullable;
import org.springtribe.framework.jobslacker.DependencyType;
import org.springtribe.framework.jobslacker.JobKey;
import org.springtribe.framework.jobslacker.SchedulingUnit;
import org.springtribe.framework.jobslacker.TriggerType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.paganini2008.devtools.beans.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 
 * TriggerDetail
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Getter
@Setter
@ToString
@JsonInclude(value = Include.NON_NULL)
public class TriggerDescription implements Serializable {

	private static final long serialVersionUID = 7719080769264307755L;

	private @Nullable Cron cron;
	private @Nullable Periodic periodic;
	private @Nullable Dependency dependency;

	public TriggerDescription() {
	}

	public TriggerDescription(String cronExpression) {
		this.cron = new Cron(cronExpression);
	}

	public TriggerDescription(long period, SchedulingUnit schedulingUnit, boolean fixedRate) {
		this.periodic = new Periodic(period, schedulingUnit, fixedRate);
	}

	@JsonInclude(value = Include.NON_NULL)
	@Accessors(chain = true)
	@Getter
	@Setter
	public static class Dependency implements Serializable {

		private static final long serialVersionUID = -8486773222061112232L;
		private @Nullable JobKey[] dependentKeys;
		private @Nullable JobKey[] subKeys;
		private @Nullable DependencyType dependencyType;
		private float completionRate = -1F;
		private TriggerType triggerType;
		private @Nullable Cron cron;
		private @Nullable Periodic periodic;

		public Dependency() {
			this.triggerType = TriggerType.NONE;
		}

		public Dependency(JobKey[] dependentKeys, DependencyType dependencyType) {
			this.dependentKeys = dependentKeys;
			this.dependencyType = dependencyType;
			this.triggerType = TriggerType.NONE;
		}

		public Dependency(JobKey[] dependentKeys, JobKey[] subKeys, DependencyType dependencyType, Float completionRate,
				TriggerType triggerType) {
			this.dependentKeys = dependentKeys;
			this.subKeys = subKeys;
			this.dependencyType = dependencyType;
			this.completionRate = completionRate;
			this.triggerType = triggerType;
		}

		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

	}

	@JsonInclude(value = Include.NON_NULL)
	@Accessors(chain = true)
	@Getter
	@Setter
	public static class Cron implements Serializable {

		private static final long serialVersionUID = -1789487585777178180L;
		private String expression;

		public Cron() {
		}

		public Cron(String expression) {
			this.expression = expression;
		}

		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	@JsonInclude(value = Include.NON_NULL)
	@Accessors(chain = true)
	@Getter
	@Setter
	public static class Periodic implements Serializable {

		private static final long serialVersionUID = 2274953049040184466L;
		private long period;
		private SchedulingUnit schedulingUnit;
		private boolean fixedRate;

		public Periodic() {
		}

		public Periodic(long period, SchedulingUnit schedulingUnit, boolean fixedRate) {
			this.period = period;
			this.schedulingUnit = schedulingUnit;
			this.fixedRate = fixedRate;
		}

		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

	}

}
