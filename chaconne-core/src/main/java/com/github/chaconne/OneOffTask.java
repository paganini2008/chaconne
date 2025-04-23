package com.github.chaconne;

/**
 * 
 * @Description: OneOffTask
 * @Author: Fred Feng
 * @Date: 04/04/2025
 * @Version 1.0.0
 */
public interface OneOffTask {

    boolean execute();

    default boolean onError(Throwable cause) {
        return true;
    }

    default void onCancellation() {};

}
