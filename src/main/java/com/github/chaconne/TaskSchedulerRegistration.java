package com.github.chaconne;

import java.util.UUID;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import com.github.chaconne.utils.NetUtils;

/**
 * 
 * @Description: TaskSchedulerRegistration
 * @Author: Fred Feng
 * @Date: 12/04/2025
 * @Version 1.0.0
 */
public class TaskSchedulerRegistration implements FactoryBean<TaskMember>, InitializingBean,
        EnvironmentAware, TaskMemberRegistration {

    protected Environment environment;
    protected TaskMember taskMember;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

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
        taskMemberInstance.setGroup(environment.getProperty("spring.application.name", "default"));
        taskMemberInstance.setHost(NetUtils.getLocalAddress().getHostAddress());
        taskMemberInstance.setPort(environment.getRequiredProperty("server.port", int.class));
        return taskMemberInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return TaskMemberInstance.class;
    }

}
