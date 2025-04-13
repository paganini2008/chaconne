package com.github.chaconne;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * @Description: RandomLb
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public class RandomLb implements LoadBalancer {

    @Override
    public <T> T selectCandidate(List<T> candidates, Object attachment) {
        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        int index = ThreadLocalRandom.current().nextInt(0, candidates.size());
        return candidates.get(index);
    }

}
