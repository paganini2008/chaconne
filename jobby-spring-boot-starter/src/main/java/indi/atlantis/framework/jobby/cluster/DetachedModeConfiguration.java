package indi.atlantis.framework.jobby.cluster;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.github.paganini2008.devtools.cron4j.TaskExecutor;
import com.github.paganini2008.devtools.cron4j.ThreadPoolTaskExecutor;
import com.github.paganini2008.devtools.jdbc.ConnectionFactory;
import com.github.paganini2008.devtools.multithreads.PooledThreadFactory;
import com.github.paganini2008.devtools.multithreads.ThreadPoolBuilder;

import indi.atlantis.framework.jobby.Banner;
import indi.atlantis.framework.jobby.BeanNames;
import indi.atlantis.framework.jobby.ConditionalOnDetachedMode;
import indi.atlantis.framework.jobby.CurrentThreadRetryPolicy;
import indi.atlantis.framework.jobby.DeclaredJobListenerBeanPostProcessor;
import indi.atlantis.framework.jobby.DefaultSchedulerStarterListener;
import indi.atlantis.framework.jobby.ExternalJobBeanLoader;
import indi.atlantis.framework.jobby.FailoverRetryPolicy;
import indi.atlantis.framework.jobby.InternalJobBeanLoader;
import indi.atlantis.framework.jobby.JdbcJobManager;
import indi.atlantis.framework.jobby.JdbcLogManager;
import indi.atlantis.framework.jobby.JdbcStopWatch;
import indi.atlantis.framework.jobby.JobAdmin;
import indi.atlantis.framework.jobby.JobAdminController;
import indi.atlantis.framework.jobby.JobBeanInitializer;
import indi.atlantis.framework.jobby.JobBeanLoader;
import indi.atlantis.framework.jobby.JobDeadlineNotification;
import indi.atlantis.framework.jobby.JobDependencyFutureListener;
import indi.atlantis.framework.jobby.JobExecutor;
import indi.atlantis.framework.jobby.JobFutureHolder;
import indi.atlantis.framework.jobby.JobIdCache;
import indi.atlantis.framework.jobby.JobManager;
import indi.atlantis.framework.jobby.JobManagerConnectionFactory;
import indi.atlantis.framework.jobby.JobRuntimeListenerContainer;
import indi.atlantis.framework.jobby.JobTimeoutResolver;
import indi.atlantis.framework.jobby.LifeCycleListenerContainer;
import indi.atlantis.framework.jobby.LoadBalancedJobBeanProcessor;
import indi.atlantis.framework.jobby.LogManager;
import indi.atlantis.framework.jobby.MailContentSource;
import indi.atlantis.framework.jobby.PrintableMailContentSource;
import indi.atlantis.framework.jobby.RetryPolicy;
import indi.atlantis.framework.jobby.ScheduleAdmin;
import indi.atlantis.framework.jobby.ScheduleManager;
import indi.atlantis.framework.jobby.Scheduler;
import indi.atlantis.framework.jobby.SchedulerErrorHandler;
import indi.atlantis.framework.jobby.SchedulerStarterListener;
import indi.atlantis.framework.jobby.SerialDependencyListener;
import indi.atlantis.framework.jobby.SerialDependencyScheduler;
import indi.atlantis.framework.jobby.SerialDependencySchedulerImpl;
import indi.atlantis.framework.jobby.SpringScheduler;
import indi.atlantis.framework.jobby.StopWatch;
import indi.atlantis.framework.jobby.TimestampTraceIdGenerator;
import indi.atlantis.framework.jobby.TraceIdGenerator;
import indi.atlantis.framework.jobby.cron4j.Cron4jScheduler;
import indi.atlantis.framework.jobby.utils.JavaMailService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DetachedModeConfiguration
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
@Configuration
public class DetachedModeConfiguration {

	static {
		Banner.printBanner("Detached Mode", log);
	}

	@Configuration
	@ConditionalOnDetachedMode(DetachedMode.PRODUCER)
	@ConditionalOnProperty(name = "atlantis.framework.jobhub.scheduler.engine", havingValue = "spring", matchIfMissing = true)
	public static class SpringSchedulerConfig {

		@Value("${atlantis.framework.jobhub.scheduler.poolSize:16}")
		private int poolSize;

		@Bean
		public Scheduler springScheduler() {
			return new SpringScheduler();
		}

		@Bean(name = BeanNames.CLUSTER_JOB_SCHEDULER, destroyMethod = "shutdown")
		public TaskScheduler taskScheduler(SchedulerErrorHandler errorHandler) {
			ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
			threadPoolTaskScheduler.setPoolSize(poolSize);
			threadPoolTaskScheduler.setThreadNamePrefix("cluster-task-scheduler-");
			threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
			threadPoolTaskScheduler.setAwaitTerminationSeconds(60);
			threadPoolTaskScheduler.setErrorHandler(errorHandler);
			return threadPoolTaskScheduler;
		}
	}

	@Configuration
	@ConditionalOnDetachedMode(DetachedMode.PRODUCER)
	@ConditionalOnProperty(name = "atlantis.framework.jobhub.scheduler.engine", havingValue = "cron4j")
	public static class Cron4jSchedulerConfig {

		@Value("${atlantis.framework.jobhub.scheduler.poolSize:16}")
		private int poolSize;

		@Bean
		public Scheduler cron4jScheduler() {
			return new Cron4jScheduler();
		}

		@ConditionalOnMissingBean(TaskExecutor.class)
		@Bean(name = BeanNames.CLUSTER_JOB_SCHEDULER, destroyMethod = "close")
		public TaskExecutor taskExecutor() {
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize,
					new PooledThreadFactory("cluster-task-scheduler-"));
			return new ThreadPoolTaskExecutor(executor);
		}
	}

	@Configuration
	@Import({ ClusterRegistryController.class, JobManagerController.class, JobAdminController.class })
	@ConditionalOnDetachedMode(DetachedMode.PRODUCER)
	public static class ProducerModeConfig {

		@Bean
		public ClusterRegistry clusterRegistry() {
			return new ClusterRegistry();
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

		@Bean
		public DeclaredJobListenerBeanPostProcessor declaredJobListenerBeanPostProcessor() {
			return new DeclaredJobListenerBeanPostProcessor();
		}

		@Bean
		public JobBeanInitializer producerModeJobBeanInitializer() {
			return new ProducerModeJobBeanInitializer();
		}

		@Bean
		public ConnectionFactory connectionFactory(DataSource dataSource) {
			return new JobManagerConnectionFactory(dataSource);
		}

		@Bean
		@ConditionalOnMissingBean(JobManager.class)
		public JobManager jobManager() {
			return new JdbcJobManager();
		}

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
		public ScheduleAdmin scheduleAdmin() {
			return new DetachedModeScheduleAdmin();
		}

		@Bean
		public SerialDependencyScheduler serialDependencyScheduler() {
			return new SerialDependencySchedulerImpl();
		}

		@Bean
		public SerialDependencyListener jobDependencyDetector() {
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

		@Bean(BeanNames.MAIN_JOB_EXECUTOR)
		public JobExecutor jobExecutor() {
			return new ProducerModeJobExecutor();
		}

		@Bean
		public SchedulerErrorHandler schedulerErrorHandler() {
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
		public JobDependencyFutureListener jobDependencyFutureListener() {
			return new JobDependencyFutureListener();
		}

		@Bean
		public JobIdCache jobIdCache(RedisConnectionFactory redisConnectionFactory, RedisSerializer<Object> redisSerializer) {
			return new JobIdCache(redisConnectionFactory, redisSerializer);
		}

		@Bean
		public TraceIdGenerator traceIdGenerator(RedisConnectionFactory redisConnectionFactory) {
			return new TimestampTraceIdGenerator(redisConnectionFactory);
		}

		@Bean
		public LogManager logManager() {
			return new JdbcLogManager();
		}

		@Bean
		public JobTimeoutResolver timeoutResolver() {
			return new JobTimeoutResolver();
		}

	}

	@Configuration
	@ConditionalOnDetachedMode(DetachedMode.CONSUMER)
	@Import({ ConsumerModeController.class })
	public static class ConsumerModeConfig {

		@Bean
		public ConsumerModeStarterListener consumerModeStarterListener() {
			return new ConsumerModeStarterListener();
		}

		@ConditionalOnMissingBean(ClusterRestTemplate.class)
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

		@Bean
		public Executor executorThreadPool(@Value("${atlantis.framework.jobhub.scheduler.executor.poolSize:16}") int maxPoolSize) {
			return ThreadPoolBuilder.common(maxPoolSize).setTimeout(-1L).setQueueSize(Integer.MAX_VALUE)
					.setThreadFactory(new PooledThreadFactory("job-executor-threads")).build();
		}

	}

	@Configuration
	@ConditionalOnDetachedMode(DetachedMode.CONSUMER)
	@ConditionalOnProperty(name = "atlantis.framework.jobhub.scheduler.running.mode", havingValue = "master-slave")
	public static class MasterSlaveConfig {

		@Bean(BeanNames.MAIN_JOB_EXECUTOR)
		public JobExecutor jobExecutor(@Qualifier("executorThreadPool") Executor threadPool) {
			ConsumerModeJobExecutor jobExecutor = new ConsumerModeJobExecutor();
			jobExecutor.setThreadPool(threadPool);
			return jobExecutor;
		}

		@Bean(BeanNames.INTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader jobBeanLoader() {
			return new InternalJobBeanLoader();
		}

		@Bean
		public RetryPolicy retryPolicy() {
			return new CurrentThreadRetryPolicy();
		}
	}

	@Configuration
	@ConditionalOnDetachedMode(DetachedMode.CONSUMER)
	@ConditionalOnProperty(name = "atlantis.framework.jobhub.scheduler.running.mode", havingValue = "loadbalance", matchIfMissing = true)
	public static class LoadBalanceConfig {

		@Bean(BeanNames.INTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader jobBeanLoader() {
			return new InternalJobBeanLoader();
		}

		@Bean(BeanNames.EXTERNAL_JOB_BEAN_LOADER)
		public JobBeanLoader externalJobBeanLoader() {
			return new ExternalJobBeanLoader();
		}

		@Bean(BeanNames.MAIN_JOB_EXECUTOR)
		public JobExecutor jobExecutor() {
			return new ConsumerModeLoadBalancer();
		}

		@Bean(BeanNames.TARGET_JOB_EXECUTOR)
		public JobExecutor consumerModeJobExecutor(@Qualifier("executorThreadPool") Executor threadPool) {
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
	@Configuration
	@ConditionalOnDetachedMode(DetachedMode.CONSUMER)
	@ConfigurationProperties(prefix = "atlantis.framework.jobhub.mail")
	public static class JavaMailConfig {

		private String host;
		private String username;
		private String password;
		private String defaultEncoding;

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
	public JobRuntimeListenerContainer jobRuntimeListenerContainer() {
		return new JobRuntimeListenerContainer();
	}

}
