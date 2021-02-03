package org.springtribe.framework.jobslacker;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.jdbc.ConnectionFactory;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JdbcLogManager
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@Slf4j
public class JdbcLogManager implements LogManager {

	@Autowired
	private ConnectionFactory connectionFactory;

	@Autowired
	private JobManager jobManager;

	@Override
	public void log(long traceId, JobKey jobKey, LogLevel logLevel, String messagePattern, Object[] args, String[] stackTraces) {
		FormattingTuple tuple = MessageFormatter.arrayFormat(messagePattern, args);
		log(traceId, logLevel, jobKey, tuple.getMessage(), stackTraces);
	}

	@Override
	public void error(long traceId, JobKey jobKey, String msg, String[] stackTraces) {
		log(traceId, LogLevel.ERROR, jobKey, msg, stackTraces);
	}

	@Override
	public void log(long traceId, LogLevel logLevel, JobKey jobKey, String msg, String[] stackTraces) {
		final int jobId = getJobId(jobKey);
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			JdbcUtils.update(connection, SqlScripts.DEF_INSERT_JOB_LOG,
					new Object[] { traceId, jobId, logLevel.name(), msg, new Timestamp(System.currentTimeMillis()) });
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
		if (ArrayUtils.isNotEmpty(stackTraces)) {
			List<Object[]> argsList = new ArrayList<Object[]>();
			for (String stackTrace : stackTraces) {
				argsList.add(new Object[] { traceId, jobId, stackTrace });
			}
			try {
				connection = connectionFactory.getConnection();
				JdbcUtils.batchUpdate(connection, SqlScripts.DEF_INSERT_JOB_EXCEPTION, argsList);
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			} finally {
				JdbcUtils.closeQuietly(connection);
			}
		}
	}

	private int getJobId(JobKey jobKey) {
		try {
			return jobManager.getJobId(jobKey);
		} catch (Exception e) {
			throw ExceptionUtils.wrapExeception(e);
		}
	}

}
