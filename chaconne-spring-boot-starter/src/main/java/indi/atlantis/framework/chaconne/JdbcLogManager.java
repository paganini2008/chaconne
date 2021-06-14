package indi.atlantis.framework.chaconne;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.ArrayUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * JdbcLogManager
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@Slf4j
public class JdbcLogManager implements LogManager {

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobDao jobDao;

	@Override
	public void log(long traceId, JobKey jobKey, LogLevel logLevel, String messagePattern, Object[] args, String[] stackTraces) {
		FormattingTuple tuple = MessageFormatter.format(messagePattern, args);
		log(traceId, logLevel, jobKey, tuple.getMessage(), stackTraces);
	}

	@Override
	public void error(long traceId, JobKey jobKey, String msg, String[] stackTraces) {
		log(traceId, LogLevel.ERROR, jobKey, msg, stackTraces);
	}

	@Override
	public void log(long traceId, LogLevel logLevel, JobKey jobKey, String msg, String[] stackTraces) {
		final int jobId = getJobId(jobKey);
		try {
			Map<String, Object> kwargs = new HashMap<String, Object>();
			kwargs.put("traceId", traceId);
			kwargs.put("jobId", jobId);
			kwargs.put("level", logLevel.name());
			kwargs.put("log", msg);
			kwargs.put("createDate", new Timestamp(System.currentTimeMillis()));
			jobDao.saveJobLog(kwargs);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if (ArrayUtils.isNotEmpty(stackTraces)) {
			List<Map<String, Object>> argsList = new ArrayList<Map<String, Object>>();
			for (String stackTrace : stackTraces) {
				Map<String, Object> kwargs = new HashMap<String, Object>();
				kwargs.put("traceId", traceId);
				kwargs.put("jobId", jobId);
				kwargs.put("stackTrace", stackTrace);
				argsList.add(kwargs);
			}
			try {
				jobDao.saveJobException(argsList);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
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
