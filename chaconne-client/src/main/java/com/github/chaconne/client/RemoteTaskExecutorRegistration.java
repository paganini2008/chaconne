package com.github.chaconne.client;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import com.github.chaconne.common.MetadataInfo;
import com.github.chaconne.common.SyncRetryer;
import com.github.chaconne.common.TaskMember;
import com.github.chaconne.common.TaskMemberInstance;
import com.github.chaconne.common.TaskMemberRegistration;
import com.github.chaconne.common.TaskMemberRequest;
import com.github.chaconne.common.utils.NetUtils;

/**
 * 
 * @Description: RemoteTaskExecutorRegistration
 * @Author: Fred Feng
 * @Date: 14/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskExecutorRegistration implements FactoryBean<TaskMember>, InitializingBean,
        EnvironmentAware, ApplicationEventPublisherAware,
        ApplicationListener<ApplicationReadyEvent>, TaskMemberRegistration {

    private static final Logger log = LoggerFactory.getLogger(RemoteTaskExecutorRegistration.class);

    private Environment environment;
    private ApplicationEventPublisher applicationEventPublisher;
    private TaskMember taskMember;
    private MetadataInfo metadataInfo;
    private ChaconneClientProperties chaconneClientProperties;

    public void setChaconneClientProperties(ChaconneClientProperties chaconneClientProperties) {
        this.chaconneClientProperties = chaconneClientProperties;
    }

    public void setMetadataInfo(MetadataInfo metadataInfo) {
        this.metadataInfo = metadataInfo;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
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
        String taskGroup = chaconneClientProperties.getTaskGroup();
        if (StringUtils.isBlank(taskGroup)) {
            taskGroup = environment.getRequiredProperty("spring.application.name");
        }
        TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
        taskMemberInstance.setMemberId(UUID.randomUUID().toString());
        taskMemberInstance.setGroup(taskGroup);
        taskMemberInstance.setHost(NetUtils.getLocalAddress().getHostAddress());
        taskMemberInstance.setPort(environment.getRequiredProperty("server.port", int.class));
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        contextPath += environment.getProperty("spring.mvc.servlet.path", "");
        taskMemberInstance.setContextPath(contextPath);
        if (metadataInfo != null) {
            taskMemberInstance.setMetadata(metadataInfo.getMetadata());
        }
        taskMemberInstance.setUptime(System.currentTimeMillis());
        return taskMemberInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return TaskMemberInstance.class;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SyncRetryer syncRetryer = event.getApplicationContext().getBean(SyncRetryer.class);
        TaskExecutorRestService taskExecutorRestService =
                event.getApplicationContext().getBean(TaskExecutorRestService.class);
        TaskMemberRequest taskMemberRequest = new TaskMemberRequest();
        taskMemberRequest.setServiceId(chaconneClientProperties.getServiceId());
        taskMemberRequest.setMemberId(taskMember.getMemberId());
        taskMemberRequest.setGroup(taskMember.getGroup());
        taskMemberRequest.setHost(taskMember.getHost());
        taskMemberRequest.setPort(taskMember.getPort());
        taskMemberRequest.setContextPath(taskMember.getContextPath());
        taskMemberRequest.setMetadata(taskMember.getMetadata());
        taskMemberRequest.setUptime(taskMember.getUptime());
        try {
            syncRetryer.runAndRetry(() -> {
                return taskExecutorRestService.registerTaskMember(taskMemberRequest);
            }, 3, Duration.ofSeconds(3), 10, null).thenAccept(result -> {
                if (result.getStatusCode().is2xxSuccessful()) {
                    applicationEventPublisher
                            .publishEvent(new TaskMemberRegisteredEvent(this, taskMember));
                }
            });
        } catch (ExecutionException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
