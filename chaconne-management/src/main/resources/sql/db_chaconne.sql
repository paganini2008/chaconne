
DROP TABLE IF EXISTS `chac_job_detail`;
CREATE TABLE `chac_job_detail` (
  `job_id` INT NOT NULL AUTO_INCREMENT,
  `cluster_name` VARCHAR(45) NOT NULL,
  `group_name` VARCHAR(45) NOT NULL,
  `job_name` VARCHAR(255) NOT NULL,
  `job_class_name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `attachment` VARCHAR(255) DEFAULT NULL,
  `email` VARCHAR(45) DEFAULT NULL,
  `retries` INT DEFAULT NULL,
  `weight` INT DEFAULT NULL,
  `timeout` BIGINT DEFAULT NULL,
  `create_date` TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (`job_id`)
);

DROP TABLE IF EXISTS `chac_job_dependency`;
CREATE TABLE `chac_job_dependency` (
  `job_id` INT NOT NULL,
  `dependent_job_id` INT NOT NULL,
  `dependency_type` INT NOT NULL
);

DROP TABLE IF EXISTS `chac_job_runtime_detail`;
CREATE TABLE `chac_job_runtime_detail` (
  `job_id` INT NOT NULL,
  `job_state` INT NOT NULL,
  `last_running_state` INT DEFAULT NULL,
  `last_execution_time` TIMESTAMP DEFAULT NULL,
  `last_completion_time` TIMESTAMP DEFAULT NULL,
  `next_execution_time` TIMESTAMP DEFAULT NULL
);

DROP TABLE IF EXISTS `chac_job_server_detail`;
CREATE TABLE `chac_job_server_detail` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cluster_name` VARCHAR(45) NOT NULL,
  `group_name` VARCHAR(45) NOT NULL,
  `instance_id` VARCHAR(255) NOT NULL,
  `context_path` VARCHAR(255) NOT NULL,
  `start_date` TIMESTAMP NOT NULL,
  `contact_person` VARCHAR(45) DEFAULT NULL,
  `contact_email` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `chac_job_trace`;
CREATE TABLE `chac_job_trace` (
  `trace_id` BIGINT NOT NULL,
  `job_id` INT NOT NULL,
  `running_state` INT DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `instance_id` VARCHAR(255) DEFAULT NULL,
  `completed` INT DEFAULT NULL,
  `failed` INT DEFAULT NULL,
  `skipped` INT DEFAULT NULL,
  `finished` INT DEFAULT NULL,
  `retries` INT DEFAULT NULL,
  `execution_time` TIMESTAMP DEFAULT NULL,
  `completion_time` TIMESTAMP DEFAULT NULL,
  `cluster_name` VARCHAR(45) DEFAULT NULL,
  `group_name` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`trace_id`)
);

DROP TABLE IF EXISTS `chac_job_exception`;
CREATE TABLE `chac_job_exception` (
  `trace_id` BIGINT NOT NULL,
  `job_id` INT NOT NULL,
  `stack_trace` VARCHAR(600) DEFAULT NULL
);

DROP TABLE IF EXISTS `chac_job_log`;
CREATE TABLE `chac_job_log` (
  `trace_id` BIGINT NOT NULL,
  `job_id` INT NOT NULL,
  `level` VARCHAR(45) DEFAULT NULL,
  `log` TEXT DEFAULT NULL,
  `create_date` TIMESTAMP DEFAULT NULL
);

DROP TABLE IF EXISTS `chac_job_trigger_detail`;
CREATE TABLE `chac_job_trigger_detail` (
  `job_id` INT NOT NULL,
  `trigger_type` INT NOT NULL,
  `trigger_description` TEXT NOT NULL,
  `start_date` TIMESTAMP DEFAULT NULL,
  `end_date` TIMESTAMP DEFAULT NULL,
  `repeat_count` INT DEFAULT NULL
);
