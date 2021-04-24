package indi.atlantis.framework.chaconne.cluster;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import indi.atlantis.framework.tridenter.EnableApplicationCluster;

/**
 * 
 * EnableChaconneDetachedMode
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableApplicationCluster(enableLeaderElection = true, enableMonitor = true)
@Import(DetachedModeConfigurationSelector.class)
public @interface EnableChaconneDetachedMode {

	DetachedMode value() default DetachedMode.CONSUMER;
}
