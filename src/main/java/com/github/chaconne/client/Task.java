package com.github.chaconne.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: Task
 * @Author: Fred Feng
 * @Date: 07/04/2025
 * @Version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Task {

    String name() default "";

    String group() default "default";

    String description() default "";

    String cron();

    long timeout() default -1L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    int maxRetryCount() default -1;

    boolean updated() default true;

}
