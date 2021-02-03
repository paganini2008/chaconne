package org.springtribe.framework.jobslacker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springtribe.framework.jobslacker.server.ServerMode;

/**
 * 
 * EnableJobSlackerApi
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DeployModeConfigurationSelector.class)
public @interface EnableJobSlackerApi {

	DeployMode value() default DeployMode.EMBEDDED;

	ServerMode serverMode() default ServerMode.CONSUMER;

}
