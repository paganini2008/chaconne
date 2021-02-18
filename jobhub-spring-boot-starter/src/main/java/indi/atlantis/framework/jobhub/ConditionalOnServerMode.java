package indi.atlantis.framework.jobhub;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

import indi.atlantis.framework.jobhub.server.ServerMode;

/**
 * 
 * ConditionalOnServerMode
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnServerModeCondition.class)
public @interface ConditionalOnServerMode {

	ServerMode value() default ServerMode.CONSUMER;
}
