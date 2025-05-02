package com.github.chaconne.cluster;

import static com.github.chaconne.jooq.tables.CronTaskQueue.CRON_TASK_QUEUE;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.context.annotation.Lazy;
import com.github.chaconne.TaskId;
import com.github.chaconne.jooq.tables.records.CronTaskQueueRecord;
import com.hazelcast.map.MapStore;

/**
 * 
 * @Description: HazelcastTaskQueueStore
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
public class HazelcastTaskQueueStore implements MapStore<LocalDateTime, Set<TaskId>> {

    private final DSLContext dsl;

    public HazelcastTaskQueueStore(@Lazy DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Set<TaskId> load(LocalDateTime key) {
        Set<TaskId> taskIds = new HashSet<TaskId>();
        Result<CronTaskQueueRecord> records = dsl.selectFrom(CRON_TASK_QUEUE)
                .where(CRON_TASK_QUEUE.FIRED_DATETIME.eq(key)).fetch();
        if (records != null && records.size() > 0) {
            for (CronTaskQueueRecord record : records) {
                taskIds.add(TaskId.of(record.getTaskGroup(), record.getTaskName()));
            }
        }
        return taskIds;
    }

    @Override
    public Map<LocalDateTime, Set<TaskId>> loadAll(Collection<LocalDateTime> keys) {
        return keys.stream().collect(HashMap::new, (m, e) -> m.put(e, load(e)), HashMap::putAll);
    }

    @Override
    public Iterable<LocalDateTime> loadAllKeys() {
        Result<Record1<LocalDateTime>> records =
                dsl.select(CRON_TASK_QUEUE.FIRED_DATETIME).from(CRON_TASK_QUEUE).fetch();
        if (records != null && records.size() > 0) {
            return records.stream().map(r -> (LocalDateTime) r.get(0)).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public void store(LocalDateTime ldt, Set<TaskId> taskIds) {
        for (TaskId taskId : taskIds) {
            dsl.insertInto(CRON_TASK_QUEUE)
                    .columns(CRON_TASK_QUEUE.FIRED_DATETIME, CRON_TASK_QUEUE.TASK_GROUP,
                            CRON_TASK_QUEUE.TASK_NAME)
                    .values(ldt, taskId.getGroup(), taskId.getName()).execute();
        }
    }

    @Override
    public void storeAll(Map<LocalDateTime, Set<TaskId>> map) {
        map.entrySet().forEach(e -> {
            store(e.getKey(), e.getValue());
        });
    }

    @Override
    public void delete(LocalDateTime ldt) {
        dsl.deleteFrom(CRON_TASK_QUEUE).where(CRON_TASK_QUEUE.FIRED_DATETIME.eq(ldt)).execute();
    }

    @Override
    public void deleteAll(Collection<LocalDateTime> keys) {
        dsl.deleteFrom(CRON_TASK_QUEUE).where(CRON_TASK_QUEUE.FIRED_DATETIME.in(keys)).execute();
    }

}
