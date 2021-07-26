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
package indi.atlantis.framework.chaconne.utils;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.paganini2008.springdessert.reditools.common.EnableRedisClient;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ChaconneMetadataConfiguration
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
@EnableRedisClient
@Configuration(proxyBeanMethods = false)
public class ChaconneMetadataConfiguration {

	@Setter
	@Configuration(proxyBeanMethods = false)
	@ConfigurationProperties(prefix = "spring.datasource")
	@ConditionalOnClass(HikariDataSource.class)
	@ConditionalOnMissingBean(DataSource.class)
	public static class DataSourceConfiguration {

		private String jdbcUrl;
		private String username;
		private String password;
		private String driverClassName;
		private int maxPoolSize = 16;

		private HikariConfig getDbConfig() {
			if (log.isTraceEnabled()) {
				log.trace("HikariDataSource JdbcUrl: " + jdbcUrl);
				log.trace("HikariDataSource Username: " + username);
				log.trace("HikariDataSource Password: " + password);
				log.trace("HikariDataSource DriverClassName: " + driverClassName);
			}
			final HikariConfig config = new HikariConfig();
			config.setDriverClassName(driverClassName);
			config.setJdbcUrl(jdbcUrl);
			config.setUsername(username);
			config.setPassword(password);
			config.setMinimumIdle(1);
			config.setMaximumPoolSize(maxPoolSize);
			config.setMaxLifetime(3 * 60 * 1000);
			config.setIdleTimeout(60 * 1000);
			config.setValidationTimeout(3000);
			config.setReadOnly(false);
			config.setConnectionInitSql("SELECT UUID()");
			config.setConnectionTestQuery("SELECT 1");
			config.setConnectionTimeout(60 * 1000);
			config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");

			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			return config;
		}

		@Bean
		public DataSource dataSource() {
			return new HikariDataSource(getDbConfig());
		}

	}

}
