package com.github.chaconne.client;

import java.util.UUID;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import com.github.chaconne.TaskMember;
import com.github.chaconne.TaskMemberInstance;
import com.github.chaconne.TaskMemberRegistration;
import com.github.chaconne.utils.NetUtils;

/**
 * 
 * @Description: RemoteTaskExecutorRegistration
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskExecutorRegistration implements FactoryBean<TaskMember>, InitializingBean,
        EnvironmentAware, ApplicationListener<ApplicationReadyEvent>, TaskMemberRegistration {

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

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        TaskExecutorRestTemplate taskRestTemplate =
                event.getApplicationContext().getBean(TaskExecutorRestTemplate.class);
        TaskMemberRequest taskMemberRequest = new TaskMemberRequest();
        taskMemberRequest.setMemberId(taskMember.getMemberId());
        taskMemberRequest.setGroup(taskMember.getGroup());
        taskMemberRequest.setHost(taskMember.getHost());
        taskMemberRequest.setPort(taskMember.getPort());
        taskMemberRequest.setContextPath(taskMember.getContextPath());
        taskRestTemplate.registerTaskMember(taskMemberRequest);
    }

}
