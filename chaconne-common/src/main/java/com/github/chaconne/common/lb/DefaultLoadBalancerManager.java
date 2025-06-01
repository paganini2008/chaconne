package com.github.chaconne.common.lb;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.github.chaconne.common.utils.ExecutorUtils;

/**
 * 
 * @Description: DefaultLoadBalancerManager
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public class DefaultLoadBalancerManager<T extends Candidate>
        implements LoadBalancerManager<T>, Runnable, InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(DefaultLoadBalancerManager.class);

    private final List<T> candidates = new CopyOnWriteArrayList<>();
    private final Set<T> activeCandidates = new CopyOnWriteArraySet<>();
    private final Map<T, AtomicInteger> inactiveCounter = new ConcurrentHashMap<>();

    private LoadBalancer loadBalancer = new RoundRobinLb();
    private Ping<T> ping = c -> true;

    @Override
    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void setPing(Ping<T> ping) {
        this.ping = ping;
    }

    private volatile T currentCandidate;
    private ScheduledExecutorService executorService;

    @Override
    public void addCandidate(T member, int weight) {
        for (int i = 0; i < weight; i++) {
            candidates.add(member);
            activeCandidates.add(member);
        }
        if (log.isTraceEnabled()) {
            log.trace("Add candidate: {}, weight: {}", member, weight);
        }
    }

    @Override
    public void removeCandidate(T member) {
        while (candidates.contains(member)) {
            candidates.remove(member);
        }
        activeCandidates.remove(member);
        if (log.isTraceEnabled()) {
            log.trace("Remove candidate: {}", member);
        }
    }

    @Override
    public int countOfCandidates() {
        return candidates.size();
    }

    @Override
    public T getCurrentCandidate() {
        return currentCandidate;
    }

    @Override
    public T getNextCandidate(Object attachment) {
        if (countOfCandidates() == 0) {
            throw new NoAvailableCandidateException(
                    attachment != null ? attachment.toString() : "");
        }
        T chosenCandidate = null;
        List<T> candidates = filterCandidates(this.candidates, attachment);
        if (candidates == null || candidates.size() == 0) {
            throw new NoAvailableCandidateException(
                    attachment != null ? attachment.toString() : "");
        }
        int n = 0;
        do {
            chosenCandidate = loadBalancer.selectCandidate(candidates, attachment);
        } while (!activeCandidates.contains(chosenCandidate) && n++ < candidates.size());
        if (!activeCandidates.contains(chosenCandidate)) {
            throw new NoAvailableCandidateException(
                    attachment != null ? attachment.toString() : "");
        }
        this.currentCandidate = chosenCandidate;
        return chosenCandidate;
    }

    protected List<T> filterCandidates(List<T> candidates, Object attachment) {
        return candidates;
    }

    @Override
    public Collection<T> getActiveCandidates() {
        return Collections.unmodifiableCollection(activeCandidates);
    }

    @Override
    public Collection<T> getInactiveCandidates() {
        return candidates.stream().filter(c -> !activeCandidates.contains(c))
                .collect(Collectors.toSet());
    }

    @Override
    public void run() {
        if (candidates.isEmpty()) {
            return;
        }
        for (T c : new HashSet<>(candidates)) {
            try {
                if (ping.isAlive(c) && shouldIgnored(c)) {
                    activeCandidates.add(c);
                } else {
                    activeCandidates.remove(c);
                    inactiveCounter.computeIfAbsent(c, k -> new AtomicInteger()).incrementAndGet();
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
                activeCandidates.remove(c);
                inactiveCounter.computeIfAbsent(c, k -> new AtomicInteger()).incrementAndGet();
            } finally {
                if (inactiveCounter.containsKey(c) && inactiveCounter.get(c).get() == 10) {
                    inactiveCounter.remove(c);
                    removeCandidate(c);
                }
            }
        }
    }

    protected boolean shouldIgnored(T c) {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executorService == null) {
            this.executorService = Executors.newSingleThreadScheduledExecutor();
            this.executorService.scheduleAtFixedRate(this, 5, 5, TimeUnit.SECONDS);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (executorService != null) {
            ExecutorUtils.gracefulShutdown(executorService, 60000L);
        }
    }

}
