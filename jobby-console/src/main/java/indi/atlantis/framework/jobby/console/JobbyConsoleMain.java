package indi.atlantis.framework.jobby.console;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.io.FileUtils;

import indi.atlantis.framework.jobby.cluster.EnableJobbyConsoleMode;

/**
 * 
 * JobbyConsoleMain
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@EnableJobbyConsoleMode
@SpringBootApplication
public class JobbyConsoleMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "indi", "atlantis", "framework", "jobby", "console");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("DEFAULT_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(JobbyConsoleMain.class, args);
		System.out.println(Env.getPid());
	}

}
