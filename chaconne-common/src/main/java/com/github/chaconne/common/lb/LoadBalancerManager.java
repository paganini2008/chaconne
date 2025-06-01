package com.github.chaconne.common.lb;

import java.util.Collection;

/**
 * 
 * @Description: LoadBalancerManager
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public interface LoadBalancerManager<T extends Candidate> {

    default void addCandidate(T candidate) {
        addCandidate(candidate, 1);
    }

    void addCandidate(T candidate, int weight);

    void removeCandidate(T candidate);

    int countOfCandidates();

    T getCurrentCandidate();

    T getNextCandidate(Object attachment);

    Collection<T> getActiveCandidates();

    Collection<T> getInactiveCandidates();

    void setPing(Ping<T> ping);

    void setLoadBalancer(LoadBalancer loadBalancer);

}
