package com.github.chaconne.common;

import java.util.List;

/**
 * 
 * @Description: NameResolver
 * @Author: Fred Feng
 * @Date: 26/05/2025
 * @Version 1.0.0
 */
public interface NameResolver {

    String getName();

    List<String> getCandidates();

}
