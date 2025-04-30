DROP TABLE IF EXISTS cron_task_detail;
CREATE TABLE cron_task_detail (
  task_name varchar(255) NOT NULL,
  task_group varchar(255) NOT NULL,
  task_class varchar(255),
  task_method varchar(255),
  url varchar(1024),
  initial_parameter text,
  description varchar(1024),
  cron_expression blob NOT NULL,
  cron varchar(255) NOT NULL,
  next_fired_datetime datetime,
  prev_fired_datetime datetime,
  task_status varchar(45) NOT NULL,
  max_retry_count int,
  timeout bigint,
  last_modified datetime DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS cron_task_log;
CREATE TABLE cron_task_log (
  task_name varchar(255) NOT NULL,
  task_group varchar(255) NOT NULL,
  task_class varchar(255),
  task_method varchar(255),
  url varchar(1024),
  initial_parameter text,
  scheduled_datetime datetime NOT NULL,
  fired_datetime datetime NOT NULL,
  return_value text,
  elapsed bigint,
  status int NOT NULL,
  error_detail text
)
