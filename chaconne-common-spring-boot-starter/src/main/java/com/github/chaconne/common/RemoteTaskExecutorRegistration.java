package com.github.chaconne.common;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.web.client.ResourceAccessException;
import com.github.chaconne.common.utils.FinalRetryer;
import com.github.chaconne.common.utils.NetUtils;

/**
 * 
 * @Description: RemoteTaskExecutorRegistration
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskExecutorRegistration extends FinalRetryer
        implements FactoryBean<TaskMember>, InitializingBean, EnvironmentAware,
        ApplicationListener<ApplicationReadyEvent>, TaskMemberRegistration {

    private static final Logger log = LoggerFactory.getLogger(RemoteTaskExecutorRegistration.class);

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
        TaskExecutorRestService taskRestTemplate =
                event.getApplicationContext().getBean(TaskExecutorRestTemplate.class);
        TaskMemberRequest taskMemberRequest = new TaskMemberRequest();
        taskMemberRequest.setMemberId(taskMember.getMemberId());
        taskMemberRequest.setGroup(taskMember.getGroup());
        taskMemberRequest.setHost(taskMember.getHost());
        taskMemberRequest.setPort(taskMember.getPort());
        taskMemberRequest.setContextPath(taskMember.getContextPath());
        Runnable r = () -> {
            taskRestTemplate.registerTaskMember(taskMemberRequest);
        };
        try {
            r.run();
        } catch (ExhaustedRetryException e) {
            if (e.getCause() instanceof ResourceAccessException) {
                retry(r);
            } else {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
