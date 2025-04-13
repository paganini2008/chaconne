package com.github.chaconne;

/**
 * 
 * @Description: LoadBalancedManager
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public interface LoadBalancedManager<T> {

    default void addCandidate(T candidate) {
        addCandidate(candidate, 1);
    }

    void addCandidate(T candidate, int weight);

    void removeCandidate(T candidate);

    int countOfCandidates();

    T getCurrentCandidate();

    T getNextCandidate(Object attachment);

    void setPing(Ping<T> ping);

    void setLoadBalancer(LoadBalancer loadBalancer);

}
