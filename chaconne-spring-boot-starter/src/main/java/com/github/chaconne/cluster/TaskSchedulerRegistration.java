package com.github.chaconne.cluster;

import java.util.UUID;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.github.chaconne.common.TaskMember;
import com.github.chaconne.common.TaskMemberInstance;
import com.github.chaconne.common.TaskMemberRegistration;
import com.github.chaconne.common.utils.NetUtils;

/**
 * 
 * @Description: TaskSchedulerRegistration
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class TaskSchedulerRegistration
        implements FactoryBean<TaskMember>, InitializingBean, TaskMemberRegistration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.servlet.context-path:}")
    private String servletContextPath;

    @Value("${spring.mvc.servlet.path:}")
    private String mvcContextPath;

    @Autowired(required = false)
    private MetadataInfo metadataInfo;

    private TaskMember taskMember;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.taskMember = createTaskMember();
    }

    @Override
    public TaskMember getObject() throws Exception {
        return taskMember;
    }

    protected TaskMember createTaskMember() {
        TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
        taskMemberInstance.setMemberId(UUID.randomUUID().toString());
        taskMemberInstance.setGroup(applicationName);
        taskMemberInstance.setHost(NetUtils.getLocalAddress().getHostAddress());
        taskMemberInstance.setPort(serverPort);
        taskMemberInstance.setContextPath(servletContextPath + mvcContextPath);
        taskMemberInstance.setUptime(System.currentTimeMillis());
        if (metadataInfo != null) {
            taskMemberInstance.setMetadata(metadataInfo.getMetadata());
        }
        return taskMemberInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return TaskMemberInstance.class;
    }

}
