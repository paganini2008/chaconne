package indi.atlantis.framework.chaconne.management;

import java.time.Duration;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * ResourceConfig
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
@Slf4j
@Configuration
public class ResourceConfig {

	@Setter
	@Configuration(proxyBeanMethods = false)
	@ConfigurationProperties(prefix = "spring.datasource")
	public static class DataSourceConfig {

		private String jdbcUrl;
		private String username;
		private String password;
		private String driverClassName;

		private HikariConfig getDbConfig() {
			if (log.isTraceEnabled()) {
				log.trace("DataSourceConfig JdbcUrl: " + jdbcUrl);
				log.trace("DataSourceConfig Username: " + username);
				log.trace("DataSourceConfig Password: " + password);
				log.trace("DataSourceConfig DriverClassName: " + driverClassName);
			}
			final HikariConfig config = new HikariConfig();
			config.setDriverClassName(driverClassName);
			config.setJdbcUrl(jdbcUrl);
			config.setUsername(username);
			config.setPassword(password);
			config.setMinimumIdle(5);
			config.setMaximumPoolSize(50);
			config.setMaxLifetime(60 * 1000);
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

		@Primary
		@Bean
		public DataSource dataSource() {
			return new HikariDataSource(getDbConfig());
		}

	}

	@Setter
	@Configuration(proxyBeanMethods = false)
	@ConfigurationProperties(prefix = "spring.redis")
	public static class RedisConfig {

		private String host;
		private String password;
		private int port;
		private int dbIndex;

		@Bean
		@ConditionalOnMissingBean(RedisConnectionFactory.class)
		public RedisConnectionFactory redisConnectionFactory() {
			RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
			redisStandaloneConfiguration.setHostName(host);
			redisStandaloneConfiguration.setPort(port);
			redisStandaloneConfiguration.setDatabase(dbIndex);
			redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
			JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
			jedisClientConfiguration.connectTimeout(Duration.ofMillis(60000)).readTimeout(Duration.ofMillis(60000)).usePooling()
					.poolConfig(jedisPoolConfig());
			JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
			return factory;
		}

		@Bean
		public JedisPoolConfig jedisPoolConfig() {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMinIdle(1);
			jedisPoolConfig.setMaxIdle(10);
			jedisPoolConfig.setMaxTotal(200);
			jedisPoolConfig.setMaxWaitMillis(-1);
			jedisPoolConfig.setTestWhileIdle(true);
			return jedisPoolConfig;
		}

		@Bean("bigint")
		public RedisTemplate<String, Long> bigintRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
			RedisTemplate<String, Long> redisTemplate = new RedisTemplate<String, Long>();
			redisTemplate.setKeySerializer(RedisSerializer.string());
			redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
			redisTemplate.setExposeConnection(true);
			redisTemplate.setConnectionFactory(redisConnectionFactory);
			redisTemplate.afterPropertiesSet();
			return redisTemplate;
		}

	}

}
