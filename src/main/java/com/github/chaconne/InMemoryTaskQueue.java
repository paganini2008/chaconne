package com.github.chaconne;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 
 * @Description: InMemoryTaskQueue
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public class InMemoryTaskQueue implements UpcomingTaskQueue {

    private final Map<LocalDateTime, Set<TaskId>> taskIds = new ConcurrentHashMap<>();

    @Override
    public boolean addTask(LocalDateTime ldt, TaskId taskId) {
        Set<TaskId> ids = taskIds.get(ldt);
        if (ids == null) {
            taskIds.putIfAbsent(ldt, new CopyOnWriteArraySet<TaskId>());
            ids = taskIds.get(ldt);
        }
        return ids.add(taskId);
    }

    @Override
    public Collection<TaskId> matchTaskIds(LocalDateTime ldt) {
        Set<TaskId> set = taskIds.remove(ldt);
        return set != null ? Collections.unmodifiableCollection(set) : Collections.emptyList();
    }

    @Override
    public int length() {
        return taskIds.size();
    }

}
