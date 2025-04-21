package com.github.chaconne.cluster;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.github.chaconne.ClockWheelAutoConfiguration;

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
@Import({ChaconneClusterConfiguration.class, ClockWheelAutoConfiguration.class})
public @interface EnableChaconne {

}
