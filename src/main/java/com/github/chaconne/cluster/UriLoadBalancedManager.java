package com.github.chaconne.cluster;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * 
 * @Description: UriLoadBalancedManager
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public class UriLoadBalancedManager extends DefaultLoadBalancedManager<URI> {

    public UriLoadBalancedManager(URI... uris) {
        if (uris != null && uris.length > 0) {
            for (URI uri : uris) {
                addCandidate(uri);
            }
        }
        setPing(new SimpleUriPing());
    }

    private static class SimpleUriPing implements Ping<URI> {

        @Override
        public boolean isAlive(URI uri) throws Exception {
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(50000);
            connection.setRequestMethod("HEAD");
            connection.connect();
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 400);
        }

    }

}
