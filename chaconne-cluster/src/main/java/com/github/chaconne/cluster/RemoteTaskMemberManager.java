package com.github.chaconne.cluster;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.SmartApplicationListener;
import com.github.chaconne.common.TaskMember;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

/**
 * 
 * @Description: RemoteTaskMemberManager
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskMemberManager implements TaskMemberManager, SmartApplicationListener,
        EntryAddedListener<String, Set<TaskMember>>, EntryUpdatedListener<String, Set<TaskMember>>,
        EntryRemovedListener<String, Set<TaskMember>>, ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(RemoteTaskMemberManager.class);

    public RemoteTaskMemberManager(HazelcastInstance hazelcastInstance) {
        this.remoteMembers = hazelcastInstance.getMap("REMOTE_TASK_MEMBERS");
        this.remoteMembers.addEntryListener(this, true);
    }

    private final Map<String, Set<TaskMember>> localMembers = new ConcurrentHashMap<>();
    private final IMap<String, Set<TaskMember>> remoteMembers;

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Set<String> getGroups() {
        Set<String> groups = new HashSet<String>();
        groups.addAll(localMembers.keySet());
        groups.addAll(remoteMembers.keySet());
        return groups;
    }

    @Override
    public Collection<TaskMember> getTaskMembers(String group) {
        if (remoteMembers.containsKey(group)) {
            return Collections.unmodifiableCollection(remoteMembers.get(group));
        }
        if (localMembers.containsKey(group)) {
            return Collections.unmodifiableCollection(localMembers.get(group));
        }
        return Collections.emptyList();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof TaskMemberAddedEvent) {
            TaskMemberAddedEvent addedEvent = (TaskMemberAddedEvent) event;
            TaskMember taskMember = addedEvent.getTaskMember();
            if (addedEvent.getClusterMode() == ClusterMode.LOCAL) {
                addTaskMember(taskMember, localMembers);
            } else {
                addTaskMember(taskMember, remoteMembers);
            }
        } else if (event instanceof TaskMemberRemovedEvent) {
            TaskMemberRemovedEvent removedEvent = (TaskMemberRemovedEvent) event;
            TaskMember taskMember = removedEvent.getTaskMember();
            if (removedEvent.getClusterMode() == ClusterMode.LOCAL) {
                removeTaskMember(taskMember, localMembers);
            } else {
                removeTaskMember(taskMember, remoteMembers);
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Local Task Members: {}", localMembers.toString());
            log.info("Remote Task Members: {}",
                    new HashMap<String, Set<TaskMember>>(remoteMembers).toString());
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(TaskMemberAddedEvent.class)
                || eventType.equals(TaskMemberRemovedEvent.class);
    }

    @Override
    public void addTaskMember(TaskMember taskMember) {
        addTaskMember(taskMember, remoteMembers);
    }

    private void addTaskMember(TaskMember taskMember, Map<String, Set<TaskMember>> store) {
        Set<TaskMember> members =
                store.computeIfAbsent(taskMember.getGroup(), k -> new CopyOnWriteArraySet<>());
        members.add(taskMember);
        store.put(taskMember.getGroup(), new CopyOnWriteArraySet<TaskMember>(members));
    }

    @Override
    public void removeTaskMember(TaskMember taskMember) {
        removeTaskMember(taskMember, remoteMembers);
    }

    private void removeTaskMember(TaskMember taskMember, Map<String, Set<TaskMember>> store) {
        Set<TaskMember> set = store.get(taskMember.getGroup());
        if (set != null) {
            set.remove(taskMember);
        }
    }

    @Override
    public void entryRemoved(EntryEvent<String, Set<TaskMember>> event) {
        Set<TaskMember> oldValues = event.getOldValue();
        Set<TaskMember> values = event.getValue();
        Collection<TaskMember> members = CollectionUtils.subtract(oldValues, values);
        if (CollectionUtils.isNotEmpty(members)) {
            TaskMember newMember = IteratorUtils.first(members.iterator());
            applicationEventPublisher
                    .publishEvent(new TaskMemberRemovedEvent(this, newMember, ClusterMode.REMOTE));
        }
    }

    @Override
    public void entryUpdated(EntryEvent<String, Set<TaskMember>> event) {
        entryAdded(event);
    }

    @Override
    public void entryAdded(EntryEvent<String, Set<TaskMember>> event) {
        Set<TaskMember> oldValues = event.getOldValue();
        if (oldValues == null) {
            oldValues = Collections.emptySet();
        }
        Set<TaskMember> newValues = event.getValue();
        Collection<TaskMember> members = CollectionUtils.subtract(newValues, oldValues);
        if (CollectionUtils.isNotEmpty(members)) {
            TaskMember newMember = IteratorUtils.first(members.iterator());
            applicationEventPublisher
                    .publishEvent(new TaskMemberAddedEvent(this, newMember, ClusterMode.REMOTE));
        }
    }

}
