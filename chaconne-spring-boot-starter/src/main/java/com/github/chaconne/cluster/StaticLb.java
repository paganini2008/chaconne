package com.github.chaconne.cluster;

import java.util.List;

/**
 * 
 * @Description: StaticLb
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public class StaticLb implements LoadBalancer {

    private final int index;

    public StaticLb(int index) {
        this.index = index;
    }

    @Override
    public <T> T selectCandidate(List<T> candidates, Object attachment) {
        if (index < candidates.size()) {
            return candidates.get(index);
        }
        return null;
    }

}
