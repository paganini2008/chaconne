DROP TABLE IF EXISTS cron_task_detail;
CREATE TABLE cron_task_detail (
  task_name varchar(255) NOT NULL,
  task_group varchar(255) NOT NULL,
  task_class varchar(255) NOT NULL,
  task_method varchar(255),
  initial_parameter text,
  description varchar(1024),
  cron_expression blob NOT NULL,
  cron varchar(255) NOT NULL,
  next_fired_datetime timestamp,
  prev_fired_datetime timestamp,
  task_status varchar(45) NOT NULL,
  max_retry_count int,
  timeout bigint,
  last_modified timestamp DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS cron_task_log;
CREATE TABLE cron_task_log (
  task_name varchar(255) NOT NULL,
  task_group varchar(255) NOT NULL,
  task_class varchar(255) NOT NULL,
  task_method varchar(255),
  initial_parameter text,
  task_status varchar(45),
  log_level varchar(45) NOT NULL,
  message varchar(1024),
  error_detail text,
  log_datetime timestamp NOT NULL
)
