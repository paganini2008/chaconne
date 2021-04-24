package indi.atlantis.framework.chaconne;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

import indi.atlantis.framework.chaconne.cluster.DetachedMode;

/**
 * 
 * ConditionalOnDetachedMode
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnDetachedModeCondition.class)
public @interface ConditionalOnDetachedMode {

	DetachedMode value() default DetachedMode.CONSUMER;
}
