package com.github.chaconne;

import java.util.List;

/**
 * 
 * @Description: LoadBalancer
 * @Author: Fred Feng
 * @Date: 16/04/2025
 * @Version 1.0.0
 */
public interface LoadBalancer {

    <T> T selectCandidate(List<T> candidates, Object attachment);

}
