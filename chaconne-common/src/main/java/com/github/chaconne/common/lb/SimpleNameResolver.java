package com.github.chaconne.common.lb;

import java.util.List;
import com.github.chaconne.common.NameResolver;

/**
 * 
 * @Description: SimpleNameResolver
 * @Author: Fred Feng
 * @Date: 25/05/2025
 * @Version 1.0.0
 */
public class SimpleNameResolver implements NameResolver {

    public SimpleNameResolver(String name, List<String> candidates) {
        this.name = name;
        this.candidates = candidates;
    }

    public SimpleNameResolver() {}

    private String name;
    private List<String> candidates;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<String> candidates) {
        this.candidates = candidates;
    }
}
