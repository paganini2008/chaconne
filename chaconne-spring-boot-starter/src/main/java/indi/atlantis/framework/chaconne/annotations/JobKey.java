package indi.atlantis.framework.chaconne.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * JobKey
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_PARAMETER)
public @interface JobKey {

	String cluster() default "";

	String group() default "";

	String name();

	String className();

}
