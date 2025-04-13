package com.github.chaconne;

import java.net.URI;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.github.chaconne.client.ApiResponse;

/**
 * 
 * @Description: TaskMemberLoadBalancedManager
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public class TaskMemberLoadBalancedManager extends DefaultLoadBalancedManager<TaskMember>
        implements SmartApplicationListener {

    public TaskMemberLoadBalancedManager() {
        setPing(new ApiPing());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof TaskMemberAddedEvent) {
            addCandidate(((TaskMemberAddedEvent) event).getTaskMember());
        } else if (event instanceof TaskMemberRemovedEvent) {
            removeCandidate(((TaskMemberAddedEvent) event).getTaskMember());
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(TaskMemberAddedEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }

    private static class ApiPing implements Ping<TaskMember> {

        private final RetryableRestTemplate restTemplate = new RetryableRestTemplate();

        @Override
        public boolean isAlive(TaskMember tm) throws Exception {
            ResponseEntity<ApiResponse<String>> responseEntity = restTemplate.exchange(
                    URI.create(String.format("%s/%s", tm.getUrl(), tm.getPingUrl())),
                    HttpMethod.GET, null, new ParameterizedTypeReference<ApiResponse<String>>() {});
            return responseEntity.getStatusCode().is2xxSuccessful()
                    && "pong".equalsIgnoreCase(responseEntity.getBody().getData());
        }

    }

}
