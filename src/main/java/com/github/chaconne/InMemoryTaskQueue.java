package com.github.chaconne;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import com.github.chaconne.utils.MapUtils;

/**
 * 
 * @Description: InMemoryTaskQueue
 * @Author: Fred Feng
 * @Date: 30/03/2025
 * @Version 1.0.0
 */
public class InMemoryTaskQueue implements UpcomingTaskQueue {

    private final Map<LocalDateTime, Set<TaskId>> queue = new ConcurrentHashMap<>();

    @Override
    public boolean addTask(LocalDateTime ldt, TaskId taskId) {
        Set<TaskId> ids = MapUtils.getOrCreate(queue, ldt, () -> new CopyOnWriteArraySet<>());
        return ids.add(taskId);
    }

    @Override
    public Collection<TaskId> matchTaskIds(LocalDateTime ldt) {
        Set<TaskId> set = queue.remove(ldt);
        return set != null ? Collections.unmodifiableCollection(set) : Collections.emptyList();
    }

    @Override
    public int length() {
        return queue.size();
    }

}
