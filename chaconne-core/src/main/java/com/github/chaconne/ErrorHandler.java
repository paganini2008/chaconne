package com.github.chaconne;

import java.time.ZonedDateTime;

/**
 * 
 * @Description: ErrorHandler
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public interface ErrorHandler {

    default void onHandleScheduler(Throwable e) {}

    default void onHandleTask(ZonedDateTime datetime, Throwable e) {}

    default void onHandleTaskResult(ZonedDateTime datetime, Throwable e) {}

}
