package indi.atlantis.framework.chaconne.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import indi.atlantis.framework.chaconne.SchedulingUnit;
import indi.atlantis.framework.chaconne.TriggerType;

/**
 * 
 * Trigger
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Trigger {

	TriggerType triggerType();

	String cron();

	long period();

	SchedulingUnit schedulingUnit();

	boolean fixedRate() default false;

}
