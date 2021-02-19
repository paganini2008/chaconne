package indi.atlantis.framework.jobby;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import indi.atlantis.framework.seafloor.EnableApplicationCluster;

/**
 * 
 * EnableJobbyEmbeddedMode
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableApplicationCluster(enableLeaderElection = true, enableMonitor = true)
@Import(EmbeddedModeConfiguration.class)
public @interface EnableJobbyEmbeddedMode {
}
