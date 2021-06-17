# Chaconne Project
### a lightweight distributed task scheduling framework
Chaconne is a lightweight distributed task scheduling framework written in Java based on spring boot framework. Adding chaconne component to your system can help you build a distributed task cluster very quickly.

### Two cluster deployment modes of chaconne
1. Decentralized deployment mode
There is no fixed scheduling center node, any node in the cluster can participate in the scheduling task
2. Centralized deployment mode
It is divided into scheduling center and task execution node, both of which support cluster

### Chaconne  consists of two parts
1. chaconne-spring-boot-starter
The core jar package contains all the core functions of chaconne
2. chaconne-console
Chaconne web management interface for task management and viewing task running status

### Implementation principle of chaconne
Chaconne relies on trident-spring-boot-starter component to realize task cluster. It uses message unicast mechanism to realize task distribution and load balancing, slice processing and other advanced features. It retains Trident's definition of cluster and supports cross cluster calls

### Chaconne feature list
1. Perfect support for spring boot framework (2.2.0 +)
2. Support <code>cron</code> expression timing task, parameter setting timing task and delay task
3. Support dynamic saving and deleting tasks
4. Support annotation saving task
5. Built in multiple load balancing algorithms and support custom load balancing algorithms
6. Support failed retrial and failed transfer
7. Support log tracking
8. Support task fragmentation
9. Support task dependency and Simple DAG
10. Support task custom termination strategy
11. Support task timeout cooling and reset
12. Support email alarm

### Install:
``` xml
		<dependency>
			<artifactId>chaconne-spring-boot-starter</artifactId>
			<groupId>indi.atlantis.framework</groupId>
			<version>1.0-RC1</version>
		</dependency>
```
### Compatibility

1. jdk1.8 (or later)
2. Spring Boot Framework 2.2.x (or later)
3. <code>Redis 4.x </code>(or later)
4. <code>MySQL 5.x</code> (or later)

### How to define a task?
1. Use annotation <code>@ChacJob</code>
2. Inherit <code>Managedjob</code> or implement <code>Job</code>  interface
3. Implement <code>NotManagedJob</code> interface

### Run Examples
**By using annotation <code>@ChacJob</code>** 

``` java
@ChacJob
@ChacTrigger(cron = "*/5 * * * * ?")
public class DemoCronJob {

	@Run
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		log.info("DemoCronJob is running at: {}", DateUtils.format(System.currentTimeMillis()));
		return RandomUtils.randomLong(1000000L, 1000000000L);
	}

	@OnSuccess
	public void onSuccess(JobKey jobKey, Object result, Logger log) {
		log.info("DemoCronJob's return value is: {}", result);
	}

	@OnFailure
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		log.error("DemoCronJob is failed by cause: {}", e.getMessage(), e);
	}

}
```

**By inheriting <code>ManagedJob</code>**

``` java
@Component
public class HealthCheckJob extends ManagedJob {

	@Override
	public Object execute(JobKey jobKey, Object arg, Logger log) {
		log.info(info());
		return UUID.randomUUID().toString();
	}

	@Override
	public Trigger getTrigger() {
		return GenericTrigger.Builder.newTrigger("*/5 * * * * ?").setStartDate(DateUtils.addSeconds(new Date(), 30)).build();
	}

	private String info() {
		long totalMemory = Runtime.getRuntime().totalMemory();
		long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
		return FileUtils.formatSize(usedMemory) + "/" + FileUtils.formatSize(totalMemory);
	}

	@Override
	public long getTimeout() {
		return 60L * 1000;
	}

}
```
**By implementing <code>NotManagedJob</code> interface**

``` java
public class EtlJob implements NotManagedJob {

	@Override
	public Object execute(JobKey jobKey, Object attachment, Logger log) {
		log.info("JobKey:{}, Parameter: {}", jobKey, attachment);
		return null;
	}

}
```
### Task Dependency
**Task dependency** is one of the important features of chaconne. Dependency patterns are divided into serial dependency and parallel dependency,
The so-called serial dependency means that task a is finished and then Task B is executed, that is, Task B depends on task A. Parallel dependency means that there are three tasks, namely task A, Task B and task C. task C can only be executed after task A and Task B are finished, which is similar to a business scenario of countersign
Based on the combination of serial dependency and parallel dependency, chaconne provides a simple DAG function to simulate business scenarios similar to workflow, which enriches the usage scenarios of task dependency

##### Serial dependency Example
``` java
@ChacJob
@ChacTrigger(triggerType = TriggerType.DEPENDENT)
@ChacDependency({ @ChacJobKey(className = "com.chinapex.test.chaconne.job.DemoSchedJob", name = "demoSchedJob") })
public class DemoDependentJob {

	@Run
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		log.info("DemoDependentJob is running at: {}", DateUtils.format(System.currentTimeMillis()));
		return RandomUtils.randomLong(1000000L, 1000000000L);
	}

	@OnSuccess
	public void onSuccess(JobKey jobKey, Object result, Logger log) {
		log.info("DemoDependentJob's return value is: {}", result);
	}

	@OnFailure
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		log.error("DemoDependentJob is failed by cause: {}", e.getMessage(), e);
	}

}
```
##### Parallel dependency Example
There are three tasks, <code>DemoTask, DemoTaskOne and DemoTaskTwo</code>
Let <code>DemoTaskOne</code> and <code>DemoTaskTwo</code> finish before executing <code>DemoTask</code>, and <code>DemoTask</code> can obtain the values of <code>DemoTaskOne</code> and <code>DemoTaskTwo</code> after execution

**<code>DemoTaskOne</code>**
``` java
@ChacJob
@ChacTrigger(triggerType = TriggerType.SIMPLE)
public class DemoTaskOne {

	@Run
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		log.info("DemoTaskOne is running at: {}", DateUtils.format(System.currentTimeMillis()));
		return RandomUtils.randomLong(1000000L, 1000000000L);
	}

	@OnSuccess
	public void onSuccess(JobKey jobKey, Object result, Logger log) {
		log.info("DemoTaskOne return value is: {}", result);
	}

	@OnFailure
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		log.error("DemoTaskOne is failed by cause: {}", e.getMessage(), e);
	}

}
```
**<code>DemoTaskTwo</code>**
``` java
@ChacJob
@ChacTrigger(triggerType = TriggerType.SIMPLE)
public class DemoTaskTwo {

	@Run
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		log.info("DemoTaskTwo is running at: {}", DateUtils.format(System.currentTimeMillis()));
		return RandomUtils.randomLong(1000000L, 1000000000L);
	}

	@OnSuccess
	public void onSuccess(JobKey jobKey, Object result, Logger log) {
		log.info("DemoTaskTwo return value is: {}", result);
	}

	@OnFailure
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		log.error("DemoTaskTwo is failed by cause: {}", e.getMessage(), e);
	}
	
}

```
**<code>DemoTask</code>**

``` java
@ChacJob
@ChacTrigger(cron = "0 0/1 * * * ?", triggerType = TriggerType.CRON)
@ChacFork({ @ChacJobKey(className = "com.chinapex.test.chaconne.job.DemoTaskOne", name = "demoTaskOne"),
		@ChacJobKey(className = "com.chinapex.test.chaconne.job.DemoTaskTwo", name = "demoTaskTwo") })
public class DemoTask {

	@Run
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		log.info("DemoTask is running at: {}", DateUtils.format(System.currentTimeMillis()));
		TaskJoinResult joinResult = (TaskJoinResult) attachment;
		TaskForkResult[] forkResults = joinResult.getTaskForkResults();
		long max = 0;
		for (TaskForkResult forkResult : forkResults) {
			max = Long.max(max, (Long) forkResult.getResult());
		}
		return max;
	}

	@OnSuccess
	public void onSuccess(JobKey jobKey, Object result, Logger log) {
		log.info("DemoTask return max value is: {}", result);
	}

	@OnFailure
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		log.error("DemoTask is failed by cause: {}", e.getMessage(), e);
	}

}
```

### Dag Task Example
**Dag Tasks currently only support API creation**
``` java
@RequestMapping("/dag")
@RestController
public class DagJobController {

	@Value("${spring.application.cluster.name}")
	private String clusterName;

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private JobManager jobManager;

	@GetMapping("/create")
	public Map<String, Object> createTask() throws Exception {
		Dag dag = new Dag(clusterName, applicationName, "testDag");
		dag.setTrigger(new CronTrigger("0 0/1 * * * ?"));
		dag.setDescription("This is only a demo of dag job");
		DagFlow first = dag.startWith(clusterName, applicationName, "demoDagStart", DemoDagStart.class.getName());
		DagFlow second = first.flow(clusterName, applicationName, "demoDag", DemoDag.class.getName());
		second.fork(clusterName, applicationName, "demoDagOne", DemoDagOne.class.getName());
		second.fork(clusterName, applicationName, "demoDagTwo", DemoDagTwo.class.getName());
		jobManager.persistJob(dag, "123");
		return Collections.singletonMap("ok", 1);
	}

}
```

### Chaconne Deployment Description
In addition to relying on the <code>springboot</code> framework, chaconne uses <code>MySQL</code> to store task information by default (currently only supports <code>MySQL</code>), and <code>Redis</code>  to save cluster metadata and message broadcast
So no matter which deployment method is used, you need to set <code>DataSource</code> and <code>RedisConnectionFactory</code>  in your application

###### 1. Decentralized Deployment of Chaconne
Add the <code>@EnableChaconneEmbeddedMode</code> annotation to the main function of your spring application and start it
**Example:**
``` java
@EnableChaconneEmbeddedMode
@SpringBootApplication
@ComponentScan
public class YourApplicationMain {

	public static void main(String[] args) {
		final int port = 8088;
		System.setProperty("server.port", String.valueOf(port));
		SpringApplication.run(YourApplicationMain.class, args);
	}

}
```
###### 2. Centralized Deployment of Chaconne
**2.1** Start the dispatch center, which requires you to create a new <code>springboot</code> project, add the annotation of <code>@EnableChaconneDetachedMode</code> on the main function, and specify it as the production end
**Example:**

``` java
@EnableChaconneDetachedMode(DetachedMode.PRODUCER)
@SpringBootApplication
public class ChaconneManagementMain {

	public static void main(String[] args) {
		SpringApplication.run(ChaconneManagementMain.class, args);
	}
}
```
*Don't forget to configure <code>DataSource</code> and <code>RedisConnectionFactory</code>*

**2.2** Add the <code>@EnableChaconneDetachedMode</code> annotation to the main function of your spring application (the default is the consumer side), and then start it
``` java
@EnableChaconneDetachedMode
@SpringBootApplication
@ComponentScan
public class YourApplicationMain {

	public static void main(String[] args) {
		final int port = 8088;
		System.setProperty("server.port", String.valueOf(port));
		SpringApplication.run(YourApplicationMain.class, args);
	}

}
```



