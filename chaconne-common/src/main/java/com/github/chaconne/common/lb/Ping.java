package com.github.chaconne.common.lb;

/**
 * 
 * @Description: Ping
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public interface Ping<T extends Candidate> {

    boolean isAlive(T candidate) throws Exception;

}
