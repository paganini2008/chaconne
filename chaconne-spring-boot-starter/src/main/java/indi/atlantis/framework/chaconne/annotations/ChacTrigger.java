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
