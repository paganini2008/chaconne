package com.github.chaconne.cluster;

import static com.github.chaconne.jooq.tables.CronTaskMember.CRON_TASK_MEMBER;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import com.github.chaconne.common.TaskMember;
import com.github.chaconne.common.TaskMemberInstance;
import com.github.chaconne.jooq.tables.records.CronTaskMemberRecord;
import com.hazelcast.map.MapStore;

/**
 * 
 * @Description: HazelcastTaskMemberStore
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
public class HazelcastTaskMemberStore implements MapStore<String, Set<TaskMember>> {

    public static final String TASK_MEMBER_STORE_NAME = "REMOTE_TASK_MEMBERS";
    private final DSLContext dsl;

    public HazelcastTaskMemberStore(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Set<TaskMember> load(String group) {
        Set<TaskMember> members = new HashSet<TaskMember>();
        Result<CronTaskMemberRecord> records = dsl.selectFrom(CRON_TASK_MEMBER)
                .where(CRON_TASK_MEMBER.MEMBER_GROUP.eq(group)).fetch();
        if (CollectionUtils.isNotEmpty(records)) {
            for (CronTaskMemberRecord record : records) {
                TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
                taskMemberInstance.setGroup(record.getMemberGroup());
                taskMemberInstance.setHost(record.getHost());
                taskMemberInstance.setPort(record.getPort());
                taskMemberInstance.setSsl(record.getIsSsl() == 1);
                taskMemberInstance.setMemberId(record.getMemberId());
                taskMemberInstance.setContextPath(record.getContextPath());
                taskMemberInstance.setUptime(record.getUptime());
                members.add(taskMemberInstance);
            }
        }
        return members;
    }

    @Override
    public Map<String, Set<TaskMember>> loadAll(Collection<String> groups) {
        Map<String, Set<TaskMember>> all = new HashMap<String, Set<TaskMember>>();
        if (CollectionUtils.isNotEmpty(groups)) {
            for (String group : groups) {
                all.put(group, load(group));
            }
        }
        return all;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        Result<Record1<String>> records =
                dsl.selectDistinct(CRON_TASK_MEMBER.MEMBER_GROUP).from(CRON_TASK_MEMBER).fetch();
        if (CollectionUtils.isNotEmpty(records)) {
            return records.stream().map(r -> (String) r.get(0)).toList();
        }
        return new ArrayList<String>();
    }

    @Override
    public void store(String group, Set<TaskMember> taskMembers) {
        if (CollectionUtils.isEmpty(taskMembers)) {
            return;
        }
        List<CronTaskMemberRecord> records = new ArrayList<CronTaskMemberRecord>();
        for (TaskMember taskMember : taskMembers) {
            CronTaskMemberRecord record = dsl.newRecord(CRON_TASK_MEMBER);
            record.setMemberGroup(taskMember.getGroup());
            record.setMemberId(taskMember.getMemberId());
            record.setHost(taskMember.getHost());
            record.setPort(taskMember.getPort());
            record.setIsSsl(taskMember.isSsl() ? (byte) 1 : 0);
            record.setContextPath(taskMember.getContextPath());
            record.setUptime(taskMember.getUptime());
            records.add(record);
        }
        dsl.batchInsert(records).execute();
    }

    @Override
    public void storeAll(Map<String, Set<TaskMember>> all) {
        if (MapUtils.isEmpty(all)) {
            return;
        }
        List<CronTaskMemberRecord> records = new ArrayList<CronTaskMemberRecord>();
        for (Set<TaskMember> taskMembers : all.values()) {
            for (TaskMember taskMember : taskMembers) {
                CronTaskMemberRecord record = dsl.newRecord(CRON_TASK_MEMBER);
                record.setMemberGroup(taskMember.getGroup());
                record.setMemberId(taskMember.getMemberId());
                record.setHost(taskMember.getHost());
                record.setPort(taskMember.getPort());
                record.setIsSsl(taskMember.isSsl() ? (byte) 1 : 0);
                record.setContextPath(taskMember.getContextPath());
                record.setUptime(taskMember.getUptime());
                records.add(record);
            }
        }
        dsl.batchInsert(records).execute();
    }

    @Override
    public void delete(String group) {
        dsl.delete(CRON_TASK_MEMBER).where(CRON_TASK_MEMBER.MEMBER_GROUP.eq(group)).execute();

    }

    @Override
    public void deleteAll(Collection<String> groups) {
        dsl.delete(CRON_TASK_MEMBER).where(CRON_TASK_MEMBER.MEMBER_GROUP.in(groups)).execute();
    }

}
