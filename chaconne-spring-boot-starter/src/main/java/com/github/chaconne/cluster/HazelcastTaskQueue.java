package com.github.chaconne.cluster;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.beans.factory.InitializingBean;
import com.github.chaconne.TaskId;
import com.github.chaconne.UpcomingTaskQueue;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

/**
 * 
 * @Description: HazelcastTaskQueue
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class HazelcastTaskQueue implements UpcomingTaskQueue, InitializingBean {

    public static final String DEFAULT_QUEUE_NAME = UpcomingTaskQueue.class.getName();

    private final HazelcastInstance hazelcastInstance;

    public HazelcastTaskQueue(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    private IMap<LocalDateTime, Set<TaskId>> queue;

    @Override
    public void afterPropertiesSet() throws Exception {
        queue = hazelcastInstance.getMap(DEFAULT_QUEUE_NAME);
    }

    @Override
    public boolean addTask(LocalDateTime ldt, TaskId taskId) {
        Set<TaskId> taskIds = queue.computeIfAbsent(ldt, k -> new CopyOnWriteArraySet<TaskId>());
        boolean result = taskIds.add(taskId);
        queue.put(ldt, taskIds);
        return result;
    }

    @Override
    public int length() {
        return queue.size();
    }

    @Override
    public Collection<TaskId> matchTaskIds(LocalDateTime ldt) {
        Set<TaskId> taskIds = queue.remove(ldt);
        return taskIds != null && taskIds.size() > 0 ? Collections.unmodifiableCollection(taskIds)
                : Collections.emptyList();
    }

}
