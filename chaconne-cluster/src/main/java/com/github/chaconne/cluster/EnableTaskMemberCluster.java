package com.github.chaconne.cluster;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 
 * @Description: EnableTaskMemberCluster
 * @Author: Fred Feng
 * @Date: 28/05/2025
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TaskMemberClusterModeSelector.class})
public @interface EnableTaskMemberCluster {

    ClusterMode mode() default ClusterMode.LOCAL;

}
