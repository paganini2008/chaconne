package com.github.chaconne;

/**
 * 
 * @Description: DefaultTaskId
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public class DefaultTaskId implements TaskId {

    private final String group;
    private final String name;

    DefaultTaskId(String group, String name) {
        this.group = group;
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return 31 * group.hashCode() * name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DefaultTaskId) {
            DefaultTaskId otherId = (DefaultTaskId) other;
            return otherId.getGroup().equals(getGroup()) && otherId.getName().equals(getName());
        }
        return false;
    }

    @Override
    public String toString() {
        return group + "#" + name;
    }

}
