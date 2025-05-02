package com.github.chaconne.cluster;

import java.util.Map;

/**
 * 
 * @Description: MetadataInfo
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
@FunctionalInterface
public interface MetadataInfo {

    Map<String, String> getMetadata();

}
