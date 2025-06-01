package com.github.chaconne.cluster;

import org.springframework.beans.BeanUtils;
import com.github.chaconne.common.TaskMember;
import com.github.chaconne.common.TaskMemberInstance;
import com.hazelcast.cluster.Member;

/**
 * 
 * @Description: ClusterUtils
 * @Author: Fred Feng
 * @Date: 21/04/2025
 * @Version 1.0.0
 */
public abstract class ClusterUtils {

    public static TaskMember getTaskMember(Member member) {
        ClusterInfo clusterInfo = new ClusterInfo(member.getAttributes());
        TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
        BeanUtils.copyProperties(clusterInfo, taskMemberInstance);
        return taskMemberInstance;
    }
}
