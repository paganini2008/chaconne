package com.github.chaconne.common;

import com.github.chaconne.common.AsyncRetryer.AsyncRetryContext;

/**
 * 
 * @Description: AsyncRetryListener
 * @Author: Fred Feng
 * @Date: 24/05/2025
 * @Version 1.0.0
 */
public interface AsyncRetryListener {

    default void begin(AsyncRetryContext retryContext) {}

    default void end(AsyncRetryContext retryContext) {}

    default void onSuccess(AsyncRetryContext retryContext, Object returnValue) {}

    default void onError(AsyncRetryContext retryContext) {}

}
