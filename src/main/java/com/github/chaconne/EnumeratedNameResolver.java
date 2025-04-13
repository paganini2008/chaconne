package com.github.chaconne;

import java.net.URI;
import java.util.List;

/**
 * 
 * @Description: EnumeratedNameResolver
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class EnumeratedNameResolver implements NameResolver {

    public EnumeratedNameResolver(String... names) {
        this.names = List.of(names);
    }

    private final List<String> names;

    @Override
    public URI retrieveOriginalUri(URI uri) {

        return null;
    }



}
