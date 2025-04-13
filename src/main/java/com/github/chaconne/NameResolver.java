package com.github.chaconne;

import java.net.URI;

/**
 * 
 * @Description: NameResolver
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public interface NameResolver {

    URI retrieveOriginalUri(URI uri);
}
