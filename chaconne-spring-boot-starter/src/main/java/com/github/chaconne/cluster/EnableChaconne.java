package com.github.chaconne.cluster;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 
 * @Description: EnableChaconne
 * @Author: Fred Feng
 * @Date: 17/04/2025
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ChaconneClusterConfiguration.class, TimeWheelAutoConfiguration.class})
public @interface EnableChaconne {

}
