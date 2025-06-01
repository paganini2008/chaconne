package com.github.chaconne.common.lb;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @Description: RoundRobinLb
 * @Author: Fred Feng
 * @Date: 16/04/2025
 * @Version 1.0.0
 */
public class RoundRobinLb implements LoadBalancer {

    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public <T> T selectCandidate(List<T> candidates, Object attachment) {
        int length;
        if ((length = candidates.size()) == 1) {
            return candidates.get(0);
        }
        int index = (int) ((counter.getAndIncrement() & Long.MAX_VALUE) % length);
        return candidates.get(index);
    }



}
