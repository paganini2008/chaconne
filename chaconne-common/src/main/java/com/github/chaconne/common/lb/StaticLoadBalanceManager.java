package com.github.chaconne.common.lb;

import java.net.URI;
import java.util.List;
import com.github.chaconne.common.NameResolver;

/**
 * 
 * @Description: StaticLoadBalanceManager
 * @Author: Fred Feng
 * @Date: 26/05/2025
 * @Version 1.0.0
 */
public class StaticLoadBalanceManager extends DefaultLoadBalancerManager<SimpleCandidate> {

    public StaticLoadBalanceManager(NameResolver... nameResolvers) {
        this.nameResolvers = nameResolvers;
    }

    private final NameResolver[] nameResolvers;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        for (NameResolver nameResolver : nameResolvers) {
            if (nameResolver.getCandidates() != null && nameResolver.getCandidates().size() > 0) {
                for (String serverAddress : nameResolver.getCandidates()) {
                    addCandidate(new SimpleCandidate(nameResolver.getName(), serverAddress));
                }
            } else {
                addCandidate(new SimpleCandidate(nameResolver.getName(), "http://localhost:6142"));
            }
        }
    }

    @Override
    protected List<SimpleCandidate> filterCandidates(List<SimpleCandidate> candidates,
            Object attachment) {
        if (attachment instanceof URI) {
            URI uri = (URI) attachment;
            String host = uri.getHost();
            return candidates.stream().filter(c -> c.getName().equalsIgnoreCase(host)).toList();
        }
        return candidates;
    }

}
