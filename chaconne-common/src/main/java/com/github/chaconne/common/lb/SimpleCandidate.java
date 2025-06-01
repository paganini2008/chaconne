package com.github.chaconne.common.lb;

/**
 * 
 * @Description: SimpleCandidate
 * @Author: Fred Feng
 * @Date: 26/05/2025
 * @Version 1.0.0
 */
public class SimpleCandidate implements Candidate {

    private final String name;
    private final String serverAddress;

    SimpleCandidate(String name, String serverAddress) {
        this.name = name;
        this.serverAddress = serverAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int hash = 1;
        hash = prime * hash + name.hashCode();
        hash = prime * hash + serverAddress.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleCandidate) {
            SimpleCandidate other = (SimpleCandidate) obj;
            return other.getName().equals(getName())
                    && other.getServerAddress().equals(getServerAddress());
        }
        return false;
    }

}
