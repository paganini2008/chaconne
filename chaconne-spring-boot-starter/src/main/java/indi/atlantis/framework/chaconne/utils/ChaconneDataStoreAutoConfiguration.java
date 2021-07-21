/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import java.time.Duration;

import javax.sql.DataSource;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.lettuce.core.RedisClient;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ChaconneDataStoreAutoConfiguration
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Slf4j
@Configuration
public class ChaconneDataStoreAutoConfiguration {

	@Setter
	@Configuration(proxyBeanMethods = false)
	@ConfigurationProperties(prefix = "chaconne.datasource")
	@ConditionalOnClass(HikariDataSource.class)
	@ConditionalOnMissingBean(DataSource.class)
	public static class DataSourceAutoConfig {

		private String jdbcUrl;
		private String username;
		private String password;
		private String driverClassName;
		private int maxPoolSize = 8;

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

	@Setter
	@Configuration(proxyBeanMethods = false)
	@ConfigurationProperties(prefix = "chaconne.redis")
	@ConditionalOnClass({ GenericObjectPool.class, RedisClient.class })
	@ConditionalOnMissingBean(RedisConnectionFactory.class)
	public static class RedisAutoConfig {

		private String host;
		private String password;
		private int port;
		private int dbIndex;
		private int maxTotalSize = 20;
		private long timeout = 60000L;

		@Bean
		public RedisConnectionFactory redisConnectionFactory() {
			RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
			redisStandaloneConfiguration.setHostName(host);
			redisStandaloneConfiguration.setPort(port);
			redisStandaloneConfiguration.setDatabase(dbIndex);
			redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
			LettuceClientConfiguration redisClientConfiguration = LettucePoolingClientConfiguration.builder()
					.commandTimeout(Duration.ofMillis(timeout)).shutdownTimeout(Duration.ofMillis(timeout)).poolConfig(redisPoolConfig())
					.build();
			LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, redisClientConfiguration);
			return factory;
		}

		@Bean
		public GenericObjectPoolConfig<?> redisPoolConfig() {
			GenericObjectPoolConfig<?> redisPoolConfig = new GenericObjectPoolConfig<>();
			redisPoolConfig.setMinIdle(1);
			redisPoolConfig.setMaxIdle(10);
			redisPoolConfig.setMaxTotal(maxTotalSize);
			redisPoolConfig.setMaxWaitMillis(-1);
			redisPoolConfig.setTestWhileIdle(true);
			return redisPoolConfig;
		}

	}

}
