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
package io.atlantisframework.chaconne.model;

import java.io.Serializable;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.paganini2008.devtools.beans.ToStringBuilder;

import io.atlantisframework.chaconne.DependencyType;
import io.atlantisframework.chaconne.JobKey;
import io.atlantisframework.chaconne.SchedulingUnit;
import io.atlantisframework.chaconne.TriggerType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 
 * TriggerDetail
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
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
		private @Nullable JobKey[] forkKeys;
		private @Nullable DependencyType dependencyType;
		private float completionRate = -1F;
		private TriggerType triggerType;
		private @Nullable Cron cron;
		private @Nullable Periodic periodic;

		public Dependency() {
			this.triggerType = TriggerType.SIMPLE;
		}

		public Dependency(JobKey[] dependentKeys, DependencyType dependencyType) {
			this.dependentKeys = dependentKeys;
			this.dependencyType = dependencyType;
			this.triggerType = TriggerType.SIMPLE;
		}

		public Dependency(JobKey[] dependentKeys, JobKey[] forkKeys, DependencyType dependencyType, Float completionRate,
				TriggerType triggerType) {
			this.dependentKeys = dependentKeys;
			this.forkKeys = forkKeys;
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
