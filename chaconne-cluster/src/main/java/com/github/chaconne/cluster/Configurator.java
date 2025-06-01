package com.github.chaconne.cluster;

import com.hazelcast.config.Config;

/**
 * 
 * @Description: Configurator
 * @Author: Fred Feng
 * @Date: 26/05/2025
 * @Version 1.0.0
 */
@FunctionalInterface
public interface Configurator {

    void applyConfig(Config config);

}
