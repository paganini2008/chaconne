/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package io.atlantisframework.chaconne;

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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import com.github.paganini2008.devtools.cron4j.TaskExecutor;
import com.github.paganini2008.devtools.cron4j.ThreadPoolTaskExecutor;
import com.github.paganini2008.devtools.jdbc.DataSourceFactory;
import com.github.paganini2008.devtools.jdbc.PooledConnectionFactory;
import com.github.paganini2008.devtools.multithreads.PooledThreadFactory;
import com.github.paganini2008.devtools.multithreads.ThreadPoolBuilder;
import com.github.paganini2008.springdesert.fastjdbc.annotations.DaoScan;

import io.atlantisframework.chaconne.cluster.ConsumerModeJobExecutor;
import io.atlantisframework.chaconne.cluster.JobManagerController;
import io.atlantisframework.chaconne.cron4j.Cron4jScheduler;
import io.atlantisframework.chaconne.utils.JavaMailService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * EmbeddedModeConfiguration
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
@DaoScan(basePackages = "io.atlantisframework.chaconne")
@Configuration(proxyBeanMethods = false)
@Import({ JobAdminController.class, JobManagerController.class })
public class EmbeddedModeConfiguration {

	static {
		Banner.printBanner("Embedded Mode", log);
	}

	@Configuration
	@ConditionalOnProperty(name = "atlantis.framework.chaconne.scheduler.running.mode", havingValue = "loadbalance", matchIfMissing = true)
	public static class LoadBalanceConfig {

		@Bean(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
		public JobExecutor jobExecutor() {
			return new EmbeddedModeLoadBalancer();
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

		@Bean(ChaconneBeanNames.EXTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader externalJobBeanLoader() {
			return new ExternalJobBeanLoader();
		}

		@Bean(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader jobBeanLoader() {
			return new InternalJobBeanLoader();
		}

		@Bean
		public RetryPolicy retryPolicy() {
			return new FailoverRetryPolicy();
		}

	}

	@Configuration
	@ConditionalOnProperty(name = "atlantis.framework.chaconne.scheduler.running.mode", havingValue = "master-slave")
	public static class MasterSlaveConfig {

		@Bean(ChaconneBeanNames.INTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader jobBeanLoader() {
			return new InternalJobBeanLoader();
		}

		@Bean(ChaconneBeanNames.MAIN_JOB_EXECUTOR)
		public JobExecutor jobExecutor(@Qualifier(ChaconneBeanNames.EXECUTOR_THREAD_POOL) Executor threadPool) {
			EmbeddedModeJobExecutor jobExecutor = new EmbeddedModeJobExecutor();
			jobExecutor.setThreadPool(threadPool);
			return jobExecutor;
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

	@ConditionalOnMissingBean(name = ChaconneBeanNames.EXECUTOR_THREAD_POOL)
	@Bean(name = ChaconneBeanNames.EXECUTOR_THREAD_POOL, destroyMethod = "shutdown")
	public Executor executorThreadPool(@Value("${atlantis.framework.chaconne.scheduler.executor.poolSize:16}") int maxPoolSize) {
		return ThreadPoolBuilder.common(maxPoolSize).setMaxPermits(maxPoolSize * 2).setTimeout(-1L)
				.setThreadFactory(new PooledThreadFactory("scheduler-executor-threads")).build();
	}

	@Configuration
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

	@Configuration
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

	@Bean(ChaconneBeanNames.SCHEDULER_ERROR_HANDLER)
	public ErrorHandler schedulerErrorHandler() {
		return new SchedulerErrorHandler();
	}

	@Bean
	public BeanExtensionAwareProcessor beanExtensionAwareProcessor() {
		return new BeanExtensionAwareProcessor();
	}

	@Bean
	public BeanAnnotationAwareProcessor beanAnnotationAwareProcessor() {
		return new BeanAnnotationAwareProcessor();
	}

	@Bean
	public JobBeanInitializer embeddedModeJobBeanInitializer() {
		return new EmbeddedModeJobBeanInitializer();
	}

	@Bean
	public SchedulerStarterListener schedulerStarterListener() {
		return new DefaultSchedulerStarterListener();
	}

	@Bean
	public EmbeddedModeStarterListener embeddedModeStarterListener() {
		return new EmbeddedModeStarterListener();
	}

	@ConditionalOnMissingBean
	@Bean
	public SchemaUpdater schemaUpdater(DataSourceFactory dataSourceFactory) throws SQLException {
		return new CreatedSchemaUpdater(new PooledConnectionFactory(dataSourceFactory.getDataSource()));
	}

	@DependsOn("schemaUpdater")
	@Bean
	@ConditionalOnMissingBean
	public JobManager jobManager() {
		return new JdbcJobManager();
	}

	@DependsOn("schemaUpdater")
	@Bean
	@ConditionalOnMissingBean
	public StopWatch stopWatch() {
		return new JdbcStopWatch();
	}

	@Bean
	public JobFutureHolder jobFutureHolder() {
		return new JobFutureHolder();
	}

	@Bean
	@ConditionalOnMissingBean
	public ScheduleManager scheduleManager() {
		return new DefaultScheduleManager();
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
	public JobDependencyUpdater jobDependencyUpdater() {
		return new JobDependencyUpdater();
	}

	@Bean
	public JobAdmin embeddedModeJobAdmin() {
		return new EmbeddedModeJobAdmin();
	}

	@Bean
	public JobDeadlineNotification jobDeadlineNotification() {
		return new JobDeadlineNotification();
	}

	@Bean
	public LifeCycleListenerContainer lifeCycleListenerContainer() {
		return new EmbeddedModeLifeCycleListenerContainer();
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
	public JobListenerContainer jobListenerContainer() {
		return new JobListenerContainer();
	}

	@Bean
	public JobTimeoutResolver jobTimeoutResolver() {
		return new EmbeddedModeJobTimeoutResolver();
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

	@Setter
	@Configuration(proxyBeanMethods = false)
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
	}

}
