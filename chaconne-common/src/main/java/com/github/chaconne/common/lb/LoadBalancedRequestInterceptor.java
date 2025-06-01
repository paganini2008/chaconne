package com.github.chaconne.common.lb;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

/**
 * 
 * @Description: LoadBalancedRequestInterceptor
 * @Author: Fred Feng
 * @Date: 25/05/2025
 * @Version 1.0.0
 */
public class LoadBalancedRequestInterceptor<T extends Candidate>
        implements ClientHttpRequestInterceptor {

    private final LoadBalancerManager<T> loadBalancerManager;

    public LoadBalancedRequestInterceptor(LoadBalancerManager<T> loadBalancerManager) {
        this.loadBalancerManager = loadBalancerManager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request) {

            @Override
            public URI getURI() {
                URI uri = super.getURI();
                String schema = uri.getScheme();
                if (!"lb".equalsIgnoreCase(schema)) {
                    return uri;
                }
                T candidate = loadBalancerManager.getNextCandidate(uri);
                return URI.create(candidate.getServerAddress() + uri.getPath());
            }

        };
        return execution.execute(requestWrapper, body);
    }

}
