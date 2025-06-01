package com.github.chaconne.cluster;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import com.github.chaconne.common.TaskMember;
import com.github.chaconne.common.lb.DefaultLoadBalancerManager;

/**
 * 
 * @Description: TaskMemberLoadBalancerManager
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public class TaskMemberLoadBalancerManager extends DefaultLoadBalancerManager<TaskMember>
        implements SmartApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(TaskMemberLoadBalancerManager.class);

    public TaskMemberLoadBalancerManager(TaskMemberManager taskMemberManager) {
        Set<String> groups = taskMemberManager.getGroups();
        if (groups != null && groups.size() > 0) {
            for (String group : groups) {
                Collection<TaskMember> taskMembers = taskMemberManager.getTaskMembers(group);
                if (taskMembers != null && taskMembers.size() > 0) {
                    for (TaskMember taskMember : taskMembers) {
                        addCandidate(taskMember);
                    }
                }
            }
        }
    }

    @Override
    protected List<TaskMember> filterCandidates(List<TaskMember> candidates, Object attachment) {
        if (attachment instanceof URI) {
            URI uri = (URI) attachment;
            String host = uri.getHost();
            return candidates.stream().filter(c -> c.getGroup().equalsIgnoreCase(host)).toList();
        }
        return candidates;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof TaskMemberAddedEvent) {
            TaskMemberAddedEvent addedEvent = (TaskMemberAddedEvent) event;
            if (addedEvent.getClusterMode() == ClusterMode.REMOTE) {
                addCandidate(addedEvent.getTaskMember());
            }
        } else if (event instanceof TaskMemberRemovedEvent) {
            TaskMemberRemovedEvent removedEvent = (TaskMemberRemovedEvent) event;
            if (removedEvent.getClusterMode() == ClusterMode.REMOTE) {
                removeCandidate(removedEvent.getTaskMember());
            }
        }
        getActiveCandidates().forEach(tm -> {
            log.info("Active Task Member: {}", tm);
        });
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(TaskMemberAddedEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }

}
