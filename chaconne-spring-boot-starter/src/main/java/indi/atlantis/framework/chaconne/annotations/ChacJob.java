package indi.atlantis.framework.chaconne.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 
 * ChacJob
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface ChacJob {

	String name() default "";

	String description() default "";

	int retries() default 0;

	int weight() default 100;

	long timeout() default -1L;

	String email() default "";

}
