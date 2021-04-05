### Jobby
A NIO Transport Framework based springboot between mircoservices 
integrate netty, mina, grizzly implementation of NIO framework

### Architecture



### Feature

| Function           | Description                                                  |
| ------------------ | ------------------------------------------------------------ |
| Job Administration | Providing a graphical interface to create new Job, delete Job, update job |
| Cluster            | Support cluster mode                                         |
| Load Balance       | Distribute job to different server in turn                   |
| Task Sharding      | Execute target method of job on different server in turn     |
| Fail retry         | Retry job with specified times if failed to run job          |
| Fail over          | Execute job on different server in turn if retry it          |
| Logging Track      | Record  per job runtime information                          |
| DAG                | support job with DAG                                         |
| Job Dependency     | Support such a occasion that one job will be triggered when another job is completed |
| Alert Mail         | Send a alert mail if job still fail after retrying some times |



### Compatibility

1. jdk1.8 (or later)
2. Spring Boot Framework 2.2.x (or later)
3. Redis 4.x (or later)
4. MySQL 5.x (or later)

### Install

```xml
<dependency>
	<groupId>indi.atlantis.framework</groupId>
	<artifactId>jobby-spring-boot-starter</artifactId>
	<version>1.0-RC1</version>
</dependency>
```



### Config

### Run
