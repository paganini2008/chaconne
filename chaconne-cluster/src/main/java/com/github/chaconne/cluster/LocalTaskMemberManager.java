package com.github.chaconne.cluster;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import com.github.chaconne.common.TaskMember;

/**
 * 
 * @Description: LocalTaskMemberManager
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public class LocalTaskMemberManager implements TaskMemberManager, SmartApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(LocalTaskMemberManager.class);

    private final Map<String, Set<TaskMember>> taskMembers = new ConcurrentHashMap<>();

    @Override
    public Set<String> getGroups() {
        return Collections.unmodifiableSet(taskMembers.keySet());
    }

    @Override
    public void addTaskMember(TaskMember taskMember) {
        Set<TaskMember> members = taskMembers.computeIfAbsent(taskMember.getGroup(),
                k -> new CopyOnWriteArraySet<>());
        members.add(taskMember);
    }

    @Override
    public Collection<TaskMember> getTaskMembers(String group) {
        if (taskMembers.containsKey(group)) {
            return Collections.unmodifiableCollection(taskMembers.get(group));
        }
        return Collections.emptyList();
    }

    @Override
    public void removeTaskMember(TaskMember taskMember) {
        Set<TaskMember> set = taskMembers.get(taskMember.getGroup());
        if (set != null) {
            set.remove(taskMember);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof TaskMemberAddedEvent) {
            TaskMemberAddedEvent addedEvent = (TaskMemberAddedEvent) event;
            if (addedEvent.getClusterMode() == ClusterMode.LOCAL) {
                TaskMember taskMember = addedEvent.getTaskMember();
                addTaskMember(taskMember);
            }
        } else if (event instanceof TaskMemberRemovedEvent) {
            TaskMemberRemovedEvent removedEvent = (TaskMemberRemovedEvent) event;
            if (removedEvent.getClusterMode() == ClusterMode.LOCAL) {
                TaskMember taskMember = removedEvent.getTaskMember();
                removeTaskMember(taskMember);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Local Task Members: {}", taskMembers.toString());
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(TaskMemberAddedEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }



}
