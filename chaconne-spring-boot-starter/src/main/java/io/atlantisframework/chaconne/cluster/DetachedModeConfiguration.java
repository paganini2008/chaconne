/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.atlantisframework.chaconne.cluster;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import com.github.paganini2008.devtools.cron4j.TaskExecutor;
import com.github.paganini2008.devtools.cron4j.ThreadPoolTaskExecutor;
import com.github.paganini2008.devtools.jdbc.DataSourceFactory;
import com.github.paganini2008.devtools.jdbc.PooledConnectionFactory;
import com.github.paganini2008.devtools.multithreads.PooledThreadFactory;
import com.github.paganini2008.devtools.multithreads.RetryableTimer;
import com.github.paganini2008.devtools.multithreads.ThreadPoolBuilder;
import com.github.paganini2008.springdesert.fastjdbc.annotations.DaoScan;

import io.atlantisframework.chaconne.Banner;
import io.atlantisframework.chaconne.BeanAnnotationAwareProcessor;
import io.atlantisframework.chaconne.BeanExtensionAwareProcessor;
import io.atlantisframework.chaconne.ChaconneBeanNames;
import io.atlantisframework.chaconne.ConditionalOnDetachedMode;
import io.atlantisframework.chaconne.CreatedSchemaUpdater;
import io.atlantisframework.chaconne.CurrentThreadRetryPolicy;
import io.atlantisframework.chaconne.DefaultSchedulerStarterListener;
import io.atlantisframework.chaconne.ExternalJobBeanLoader;
import io.atlantisframework.chaconne.FailoverRetryPolicy;
import io.atlantisframework.chaconne.InternalJobBeanLoader;
import io.atlantisframework.chaconne.JdbcJobManager;
import io.atlantisframework.chaconne.JdbcLogManager;
import io.atlantisframework.chaconne.JdbcStopWatch;
import io.atlantisframework.chaconne.JobAdmin;
import io.atlantisframework.chaconne.JobAdminController;
import io.atlantisframework.chaconne.JobBeanInitializer;
import io.atlantisframework.chaconne.JobBeanLoader;
import io.atlantisframework.chaconne.JobDeadlineNotification;
import io.atlantisframework.chaconne.JobDependencyUpdater;
import io.atlantisframework.chaconne.JobExecutor;
import io.atlantisframework.chaconne.JobFutureHolder;
import io.atlantisframework.chaconne.JobIdCache;
import io.atlantisframework.chaconne.JobListenerContainer;
import io.atlantisframework.chaconne.JobManager;
import io.atlantisframework.chaconne.JobTimeoutResolver;
import io.atlantisframework.chaconne.LifeCycleListenerContainer;
import io.atlantisframework.chaconne.LoadBalancedJobBeanProcessor;
import io.atlantisframework.chaconne.LogManager;
import io.atlantisframework.chaconne.MailContentSource;
import io.atlantisframework.chaconne.PrintableMailContentSource;
import io.atlantisframework.chaconne.RetryPolicy;
import io.atlantisframework.chaconne.ScheduleManager;
import io.atlantisframework.chaconne.Scheduler;
import io.atlantisframework.chaconne.SchedulerErrorHandler;
import io.atlantisframework.chaconne.SchedulerStarterListener;
import io.atlantisframework.chaconne.SchemaUpdater;
import io.atlantisframework.chaconne.SerialDependencyListener;
import io.atlantisframework.chaconne.SerialDependencyScheduler;
import io.atlantisframework.chaconne.SerialDependencySchedulerImpl;
import io.atlantisframework.chaconne.SpringScheduler;
import io.atlantisframework.chaconne.StopWatch;
import io.atlantisframework.chaconne.TimeBasedTraceIdGenerator;
import io.atlantisframework.chaconne.TraceIdGenerator;
import io.atlantisframework.chaconne.cron4j.Cron4jScheduler;
import io.atlantisframework.chaconne.utils.ChaconneMetadataConfiguration;
import io.atlantisframework.chaconne.utils.JavaMailService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DetachedModeConfiguration
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class DetachedModeConfiguration {

	static {
		Banner.printBanner("Detached Mode", log);
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnDetachedMode(DetachedMode.PRODUCER)
	@ConditionalOnProperty(name = "atlantis.framework.chaconne.scheduler.engine", havingValue = "spring")
	public static class SpringSchedulerConfig {

		@Value("${atlantis.framework.chaconne.scheduler.poolSize:16}")
		private int poolSize;

		@Bean
		public Scheduler springScheduler() {
			return new SpringScheduler();
		}

		@ConditionalOnMissingBean(name = ChaconneBeanNames.JOB_SCHEDULER)
		@Bean(name = ChaconneBeanNames.JOB_SCHEDULER, destroyMethod = "shutdown")
		public TaskScheduler taskScheduler(@Qualifier(ChaconneBeanNames.SCHEDULER_ERROR_HANDLER) ErrorHandler errorHandler) {
			ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
			threadPoolTaskScheduler.setPoolSize(poolSize);
			threadPoolTaskScheduler.setThreadNamePrefix("chaconne-task-scheduler-");
			threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
			threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
			threadPoolTaskScheduler.setErrorHandler(errorHandler);
			return threadPoolTaskScheduler;
		}
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnDetachedMode(DetachedMode.PRODUCER)
	@ConditionalOnProperty(name = "atlantis.framework.chaconne.scheduler.engine", havingValue = "cron4j", matchIfMissing = true)
	public static class Cron4jSchedulerConfig {

		@Value("${atlantis.framework.chaconne.scheduler.poolSize:16}")
		private int poolSize;

		@Bean
		public Scheduler cron4jScheduler() {
			return new Cron4jScheduler();
		}

		@ConditionalOnMissingBean(name = ChaconneBeanNames.JOB_SCHEDULER)
		@Bean(name = ChaconneBeanNames.JOB_SCHEDULER, destroyMethod = "close")
		public TaskExecutor taskScheduler() {
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize,
					new PooledThreadFactory("chaconne-task-scheduler-"));
			return new ThreadPoolTaskExecutor(executor);
		}
	}

	@Configuration(proxyBeanMethods = false)
	@Import({ ChaconneMetadataConfiguration.class, JobServerRegistryController.class, JobManagerController.class,
			JobAdminController.class })
	@EnableRetry
	@DaoScan(basePackages = "io.atlantisframework.chaconne")
	@ConditionalOnDetachedMode(DetachedMode.PRODUCER)
	public static class ProducerModeConfig {

		@ConditionalOnMissingBean
		@Bean
		public SchemaUpdater schemaUpdater(DataSourceFactory dataSourceFactory) throws SQLException {
			return new CreatedSchemaUpdater(new PooledConnectionFactory(dataSourceFactory.getDataSource()));
		}

		@DependsOn("schemaUpdater")
		@Bean
		public JobServerRegistry jobServerRegistry() {
			return new JobServerRegistry();
		}

		@ConditionalOnMissingBean
		@Bean
		public ClusterRestTemplate clusterRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
			return new ProducerModeRestTemplate(httpRequestFactory);
		}

		@Bean
		public SchedulerStarterListener schedulerStarterListener() {
			return new DefaultSchedulerStarterListener();
		}

		@DependsOn("schemaUpdater")
		@Bean
		public BeanExtensionAwareProcessor beanExtensionAwareProcessor() {
			return new BeanExtensionAwareProcessor();
		}

		@DependsOn("schemaUpdater")
		@Bean
		public BeanAnnotationAwareProcessor beanAnnotationAwareProcessor() {
			return new BeanAnnotationAwareProcessor();
		}

		@DependsOn("schemaUpdater")
		@Bean
		public JobBeanInitializer producerModeJobBeanInitializer() {
			return new ProducerModeJobBeanInitializer();
		}

		@DependsOn("schemaUpdater")
		@Bean
		@ConditionalOnMissingBean(JobManager.class)
		public JobManager jobManager() {
			return new JdbcJobManager();
		}

		@DependsOn("schemaUpdater")
		@Bean
		@ConditionalOnMissingBean(StopWatch.class)
		public StopWatch stopWatch() {
			return new JdbcStopWatch();
		}

		@Bean
		public JobAdmin jobAdmin() {
			return new DetachedModeJobAdmin();
		}

		@Bean
		public SerialDependencyScheduler serialDependencyScheduler() {
			return new SerialDependencySchedulerImpl();
		}

		@Bean
		public SerialDependencyListener serialDependencyListener() {
			return new SerialDependencyListener();
		}

		@Bean
		@ConditionalOnMissingBean(ScheduleManager.class)
		public ScheduleManager scheduleManager() {
			return new DetachedModeScheduleManager();
		}

		@Bean
		public JobFutureHolder jobFutureHolder() {
			return new JobFutureHolder();
		}

		@Bean
		public JobBeanLoader jobBeanLoader() {
			return new DetachedModeJobBeanLoader();
		}

		@Bean(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
		public JobExecutor jobExecutor() {
			return new ProducerModeJobExecutor();
		}

		@Bean(ChaconneBeanNames.SCHEDULER_ERROR_HANDLER)
		public ErrorHandler schedulerErrorHandler() {
			return new SchedulerErrorHandler();
		}

		@Bean
		public JobDeadlineNotification jobDeadlineNotification() {
			return new JobDeadlineNotification();
		}

		@Bean
		public LifeCycleListenerContainer lifeCycleListenerContainer() {
			return new DetachedModeLifeCycleListenerContainer();
		}

		@Bean
		public JobDependencyUpdater jobDependencyUpdater() {
			return new RemoteJobDependencyUpdater();
		}

		@Bean
		public JobIdCache jobIdCache(RedisConnectionFactory redisConnectionFactory, RedisSerializer<Object> redisSerializer) {
			return new JobIdCache(redisConnectionFactory, redisSerializer);
		}

		@Bean
		public TraceIdGenerator traceIdGenerator(RedisConnectionFactory redisConnectionFactory) {
			return new TimeBasedTraceIdGenerator(redisConnectionFactory);
		}

		@DependsOn("schemaUpdater")
		@Bean
		public LogManager logManager() {
			return new JdbcLogManager();
		}

		@Bean
		public JobTimeoutResolver jobTimeoutResolver() {
			return new DetachedModeJobTimeoutResolver();
		}

		@Bean
		public ContextPathAccessor contextPathAccessor() {
			return new ContextPathAccessor();
		}

		@Bean(destroyMethod = "cancel")
		public RetryableTimer retryableTimer() {
			return new RetryableTimer();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnDetachedMode(DetachedMode.CONSUMER)
	@Import({ ConsumerModeController.class })
	public static class ConsumerModeConfig {

		@Bean
		public ConsumerModeStarterListener consumerModeStarterListener() {
			return new ConsumerModeStarterListener();
		}

		@Bean
		public BeanAnnotationAwareProcessor beanAnnotationAwareProcessor() {
			return new BeanAnnotationAwareProcessor();
		}

		@Bean(destroyMethod = "cancel")
		public RetryableTimer retryableTimer() {
			return new RetryableTimer();
		}

		@ConditionalOnMissingBean
		@Bean
		public ClusterRestTemplate clusterRestTemplate(ClientHttpRequestFactory httpRequestFactory) {
			return new ConsumerModeRestTemplate(httpRequestFactory);
		}

		@Bean
		public ConsumerModeJobBeanInitializer consumerModeJobBeanListener() {
			return new ConsumerModeJobBeanInitializer();
		}

		@Bean
		public SchedulerStarterListener consumerModeSchedulerStarterListener() {
			return new ConsumerModeSchedulerStarterListener();
		}

		@Bean
		public JobManager jobManager() {
			return new RestJobManager();
		}

		@Bean
		public StopWatch stopWatch() {
			return new RestStopWatch();
		}

		@Bean
		public SerialDependencyScheduler serialDependencyScheduler() {
			return new SerialDependencySchedulerImpl();
		}

		@Bean
		public SerialDependencyListener serialDependencyListener() {
			return new SerialDependencyListener();
		}

		@Bean
		public JobFutureHolder jobFutureHolder() {
			return new JobFutureHolder();
		}

		@Bean
		public JobAdmin jobAdmin() {
			return new ConsumerModeJobAdmin();
		}

		@Bean
		public TraceIdGenerator traceIdGenerator() {
			return new RestTraceIdGenerator();
		}

		@Bean
		public LogManager logManager() {
			return new RestLogManager();
		}

		@Bean
		@ConditionalOnBean(JavaMailSenderImpl.class)
		public JavaMailService javaMailService() {
			return new JavaMailService();
		}

		@Bean
		@ConditionalOnMissingBean(MailContentSource.class)
		public MailContentSource printableMailContentSource() {
			return new PrintableMailContentSource();
		}

		@ConditionalOnMissingBean(name = ChaconneBeanNames.EXECUTOR_THREAD_POOL)
		@Bean(name = ChaconneBeanNames.EXECUTOR_THREAD_POOL, destroyMethod = "shutdown")
		public Executor executorThreadPool(@Value("${atlantis.framework.chaconne.scheduler.executor.poolSize:16}") int maxPoolSize) {
			return ThreadPoolBuilder.common(maxPoolSize).setMaxPermits(maxPoolSize * 2).setTimeout(-1L)
					.setThreadFactory(new PooledThreadFactory("scheduler-executor-threads")).build();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnDetachedMode(DetachedMode.CONSUMER)
	@ConditionalOnProperty(name = "atlantis.framework.chaconne.scheduler.running.mode", havingValue = "master-slave")
	public static class MasterSlaveConfig {

		@Bean(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
		public JobExecutor jobExecutor(@Qualifier(ChaconneBeanNames.EXECUTOR_THREAD_POOL) Executor threadPool) {
			ConsumerModeJobExecutor jobExecutor = new ConsumerModeJobExecutor();
			jobExecutor.setThreadPool(threadPool);
			return jobExecutor;
		}

		@Bean(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader jobBeanLoader() {
			return new InternalJobBeanLoader();
		}

		@Bean
		public RetryPolicy retryPolicy() {
			return new CurrentThreadRetryPolicy();
		}

		@ConditionalOnMissingBean(name = ChaconneBeanNames.MAIN_THREAD_POOL)
		@Bean(name = ChaconneBeanNames.MAIN_THREAD_POOL, destroyMethod = "shutdown")
		public Executor mainThreadPool(@Value("${atlantis.framework.chaconne.scheduler.poolSize:16}") int maxPoolSize) {
			return ThreadPoolBuilder.common(maxPoolSize).setTimeout(-1L).setThreadFactory(new PooledThreadFactory("scheduler-main-threads"))
					.build();
		}
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnDetachedMode(DetachedMode.CONSUMER)
	@ConditionalOnProperty(name = "atlantis.framework.chaconne.scheduler.running.mode", havingValue = "loadbalance", matchIfMissing = true)
	public static class LoadBalanceConfig {

		@Bean(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader jobBeanLoader() {
			return new InternalJobBeanLoader();
		}

		@Bean(ChaconneBeanNames.EXTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader externalJobBeanLoader() {
			return new ExternalJobBeanLoader();
		}

		@Bean(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
		public JobExecutor jobExecutor() {
			return new ConsumerModeLoadBalancer();
		}

		@Bean(ChaconneBeanNames.TARGET_JOB_EXECUTOR)
		public JobExecutor targetJobExecutor(@Qualifier(ChaconneBeanNames.EXECUTOR_THREAD_POOL) Executor threadPool) {
			ConsumerModeJobExecutor jobExecutor = new ConsumerModeJobExecutor();
			jobExecutor.setThreadPool(threadPool);
			return jobExecutor;
		}

		@Bean
		public LoadBalancedJobBeanProcessor loadBalancedJobBeanProcessor() {
			return new LoadBalancedJobBeanProcessor();
		}

		@Bean
		public RetryPolicy retryPolicy() {
			return new FailoverRetryPolicy();
		}

	}

	@Setter
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnDetachedMode(DetachedMode.CONSUMER)
	@ConditionalOnClass(JavaMailSender.class)
	@ConfigurationProperties(prefix = "atlantis.framework.chaconne.mail")
	public static class JavaMailConfig {

		private String host;
		private String username;
		private String password;
		private String defaultEncoding;

		@ConditionalOnMissingBean(name = "jobMailSender")
		@Bean
		public JavaMailSender jobMailSender() {
			JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
			javaMailSender.setHost(host);
			javaMailSender.setUsername(username);
			javaMailSender.setPassword(password);
			javaMailSender.setDefaultEncoding(defaultEncoding);
			Properties javaMailProperties = new Properties();
			javaMailProperties.put("mail.smtp.auth", true);
			javaMailProperties.put("mail.smtp.starttls.enable", false);
			javaMailProperties.put("mail.smtp.starttls.required", false);
			javaMailProperties.put("mail.smtp.timeout", 60000);
			javaMailSender.setJavaMailProperties(javaMailProperties);
			return javaMailSender;
		}

		@Bean
		@ConditionalOnBean(JavaMailSender.class)
		public JavaMailService javaMailService() {
			return new JavaMailService();
		}

		@Bean
		@ConditionalOnMissingBean(MailContentSource.class)
		public MailContentSource printableMailContentSource() {
			return new PrintableMailContentSource();
		}
	}

	@Bean
	public JobListenerContainer jobListenerContainer() {
		return new JobListenerContainer();
	}

}
