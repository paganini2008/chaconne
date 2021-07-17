/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
package indi.atlantis.framework.chaconne.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import indi.atlantis.framework.chaconne.SchedulingUnit;
import indi.atlantis.framework.chaconne.TriggerType;

/**
 * 
 * ChacTrigger
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ChacTrigger {

	TriggerType triggerType() default TriggerType.CRON;

	String cron() default "";

	long period() default 0L;

	SchedulingUnit schedulingUnit() default SchedulingUnit.SECONDS;

	boolean fixedRate() default false;

	long delay() default 0L;

	String startDate() default "";

	String endDate() default "";

	int repeatCount() default -1;

}
