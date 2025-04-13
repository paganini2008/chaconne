package com.github.chaconne;

import java.net.URI;

/**
 * 
 * @Description: NoOpNameResolver
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class NoOpNameResolver implements NameResolver {

    @Override
    public URI retrieveOriginalUri(URI uri) {
        return uri;
    }

}
