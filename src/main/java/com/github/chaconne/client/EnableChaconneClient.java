package com.github.chaconne.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 
 * @Description: EnableChaconneClient
 * @Author: Fred Feng
 * @Date: 17/04/2025
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ChaconneClientAutoConfiguration.class})
public @interface EnableChaconneClient {
}
