package com.github.chaconne;

/**
 * 
 * @Description: Ping
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public interface Ping<T> {

    boolean isAlive(T candidate) throws Exception;

}
