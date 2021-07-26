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
package indi.atlantis.framework.chaconne;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * DdlScripts
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class DdlScripts {

	public static abstract class CreateScripts {

		static final String DEF_DDL_JOB_SERVER_DETAIL = "create table chac_job_server_detail(id int primary key auto_increment, cluster_name varchar(45) not null, group_name varchar(45) not null, instance_id varchar(255) not null, context_path varchar(255) not null, start_date timestamp not null, contact_person varchar(45), contact_email varchar(255))";
		static final String DEF_DDL_JOB_DETAIL = "create table chac_job_detail(job_id int primary key auto_increment, cluster_name varchar(45) not null, group_name varchar(45) not null, job_name varchar(255) not null, job_class_name varchar(255) not null, description varchar(255), attachment varchar(255), email varchar(45), retries int, weight int, timeout bigint, create_date timestamp)";
		static final String DEF_DDL_JOB_TRIGGER_DETAIL = "create table chac_job_trigger_detail(job_id int not null, trigger_type int not null, trigger_description text not null, start_date timestamp, end_date timestamp, repeat_count int)";
		static final String DEF_DDL_JOB_RUNTIME_DETAIL = "create table chac_job_runtime_detail(job_id int not null, job_state int not null, last_running_state int, last_execution_time timestamp, last_completion_time timestamp, next_execution_time timestamp)";
		static final String DEF_DDL_JOB_TRACE = "create table chac_job_trace(trace_id bigint primary key,cluster_name varchar(45),group_name varchar(45),job_id int not null, running_state int, address varchar(255), instance_id varchar(255), completed int, failed int, skipped int, finished int, retries int, execution_time timestamp, completion_time timestamp)";
		static final String DEF_DDL_JOB_EXCEPTION = "create table chac_job_exception(trace_id bigint not null, job_id int not null, stack_trace varchar(600))";
		static final String DEF_DDL_JOB_LOG = "create table chac_job_log(trace_id bigint not null, job_id int not null, level varchar(45), log text, create_date timestamp)";
		static final String DEF_DDL_JOB_DEPENDENCY = "create table chac_job_dependency(job_id int not null, dependent_job_id int not null, dependency_type int not null)";

		public static Map<String, String> ddls() {
			Map<String, String> ddls = new HashMap<String, String>();
			ddls.put("chac_job_server_detail", DEF_DDL_JOB_SERVER_DETAIL);
			ddls.put("chac_job_detail", DEF_DDL_JOB_DETAIL);
			ddls.put("chac_job_trigger_detail", DEF_DDL_JOB_TRIGGER_DETAIL);
			ddls.put("chac_job_runtime_detail", DEF_DDL_JOB_RUNTIME_DETAIL);
			ddls.put("chac_job_trace", DEF_DDL_JOB_TRACE);
			ddls.put("chac_job_exception", DEF_DDL_JOB_EXCEPTION);
			ddls.put("chac_job_log", DEF_DDL_JOB_LOG);
			ddls.put("chac_job_dependency", DEF_DDL_JOB_DEPENDENCY);
			return ddls;
		}
	}

	public static abstract class DropScripts {

		static final String DEF_DDL_JOB_SERVER_DETAIL = "drop table chac_job_server_detail";
		static final String DEF_DDL_JOB_DETAIL = "drop table chac_job_detail";
		static final String DEF_DDL_JOB_TRIGGER_DETAIL = "drop table chac_job_trigger_detail";
		static final String DEF_DDL_JOB_RUNTIME_DETAIL = "drop table chac_job_runtime_detail";
		static final String DEF_DDL_JOB_TRACE = "drop table chac_job_trace";
		static final String DEF_DDL_JOB_EXCEPTION = "drop table chac_job_exception";
		static final String DEF_DDL_JOB_LOG = "drop table chac_job_log";
		static final String DEF_DDL_JOB_DEPENDENCY = "drop table chac_job_dependency";

		public static Map<String, String> ddls() {
			Map<String, String> ddls = new HashMap<String, String>();
			ddls.put("chac_job_server_detail", DEF_DDL_JOB_SERVER_DETAIL);
			ddls.put("chac_job_detail", DEF_DDL_JOB_DETAIL);
			ddls.put("chac_job_trigger_detail", DEF_DDL_JOB_TRIGGER_DETAIL);
			ddls.put("chac_job_runtime_detail", DEF_DDL_JOB_RUNTIME_DETAIL);
			ddls.put("chac_job_trace", DEF_DDL_JOB_TRACE);
			ddls.put("chac_job_exception", DEF_DDL_JOB_EXCEPTION);
			ddls.put("chac_job_log", DEF_DDL_JOB_LOG);
			ddls.put("chac_job_dependency", DEF_DDL_JOB_DEPENDENCY);
			return ddls;
		}
	}

}
