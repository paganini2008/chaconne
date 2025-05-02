package com.github.chaconne.cluster;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: LoadBalancingUriRewriter
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
public class LoadBalancingUriRewriter implements ClientHttpRequestInterceptor {

    private final LoadBalancerManager<TaskMember> taskMemberLoadBalancerManager;

    public LoadBalancingUriRewriter(LoadBalancerManager<TaskMember> taskMemberLoadBalancerManager) {
        super();
        this.taskMemberLoadBalancerManager = taskMemberLoadBalancerManager;
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
                TaskMember taskMember = taskMemberLoadBalancerManager.getNextCandidate(uri);
                return URI.create(taskMember.getUrl() + uri.getPath());
            }

        };
        return execution.execute(requestWrapper, body);
    }

}
