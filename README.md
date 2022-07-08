# Chaconne Framework
### a lightweight distributed task scheduling framework

**Chaconne** is a lightweight distributed task scheduling framework based on <code>SpringBoot</code>  framework. It not only has high availability and scalability but also is easy to customize your business application by API provided.

## Features

* Perfectly supporting for <code>SpringBoot</code> framework (2.2.x or later)
* Supporting master-slave cluster mode and load-balance cluster mode for scheduling tasks
* Providing rich APIs for defining a task
* Exposing plenty of  external user interfaces to get information about cluster, task and task state
* Supporting dynamically saving, pausing and deleting tasks
* Supporting retry after task failure and failure transferring
* Supporting task logs saving and tracking
* Supporting task segmentation and  recombination of execution results
* Providing APIs to build DAG to describe dependency relationship between tasks
* Supporting to customize task termination policy
* Supporting to freeze and reset a task if has run a long time
* Supporting email alarm if task running encounters exceptions

## Modules

* chaconne-spring-boot-starter
The core class library of chaconne framework, providing implementations of most of core function and a series of external user API, including cluster management, task management and task runtime management.

* chaconne-manager
If being deployed with decentralized mode, chaconne framework provides an example application to act scheduler role  to call task executors. 

* chaconne-console
Chaconne framework provides a  web application to display task info, running data and statistical data.

## Deploy

* Decentralized Deployment Mode
No fixed and specific scheduler role, any application in the chaconne cluster can play the scheduler or executor role during the period of task scheduling.

* Centralized Deployment Mode
Differentiating the applications in the chaconne cluster as fixed scheduler and executor role during the period of task scheduling.

## Install
``` xml
<dependency>
    <artifactId>chaconne-spring-boot-starter</artifactId>
    <groupId>indi.atlantis.framework</groupId>
    <version>1.0-RC1</version>
</dependency>
```

## Compatibility

* Jdk1.8 (or later)
* <code>Spring Boot</code> Framework 2.2.x (or later)
* <code>Redis 4.x </code> (or later)
* <code>MySQL 5.x</code> (or later)

## Quick Start

#### How to define a task ?
**Example 1**

``` java
@ChacJob
@ChacTrigger(cron = "*/5 * * * * ?")
public class CronJob {

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



**Example 2**

``` java
@Component
public class MemoryCheckJob extends ManagedJob {

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


**Example 3**

``` java
public class CommonJob implements NotManagedJob {

	@Override
	public Object execute(JobKey jobKey, Object attachment, Logger log) {
		log.info("JobKey:{}, Parameter: {}", jobKey, attachment);
		return null;
	}

}
```

#### Task Dependency and DAG

**Task dependency** is one of the important features of chaconne. Dependency patterns are divided into serial dependency and parallel dependency,
The so-called serial dependency means that task a is finished and then Task B is executed, that is, Task B depends on task A. Parallel dependency means that there are three tasks, namely task A, Task B and task C. task C can only be executed after task A and Task B are finished, which is similar to a business scenario of countersign
Based on the combination of serial dependency and parallel dependency, chaconne provides a simple DAG function to simulate business scenarios similar to workflow, which enriches the usage scenarios of task dependency



``` mermaid
flowchart TD;
     Task-A-->Task-B;
     Task-B-->Task-C;

```





##### Serial dependency Example

TaskA.java

``` java
package com.test.chaconne

@ChacJob(name = "taskA")
@ChacTrigger(cron = "0 */1 * * * ?")
public class TaskA {

	@Run
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		log.info("TaskA is running at: {}", DateUtils.format(System.currentTimeMillis()));
		return "TaskA";
	}

}
```



TaskB.java

``` java
@ChacJob(name = "taskB")
@ChacTrigger(triggerType = TriggerType.DEPENDENT)
@ChacDependency({ @ChacJobKey(className = "com.test.chaconne.TaskA", name = "taskA") })
public class TaskB {

	@Run
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		log.info("TaskB is running at: {}", DateUtils.format(System.currentTimeMillis()));
		return "TaskB";
	}

	@OnSuccess
	public void onSuccess(JobKey jobKey, Object result, Logger log) {
		log.info("TaskB's return value is: {}", result);
	}

	@OnFailure
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		log.error("TaskB is failed by cause: {}", e.getMessage(), e);
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



