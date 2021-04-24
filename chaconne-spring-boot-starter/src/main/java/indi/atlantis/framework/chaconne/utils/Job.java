package indi.atlantis.framework.chaconne.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Job
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Job {

	String name();

	String description();

	int retries();

	int weight();

	long timeout();

	String email();

}
