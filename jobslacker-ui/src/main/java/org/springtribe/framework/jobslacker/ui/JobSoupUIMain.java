package org.springtribe.framework.jobslacker.ui;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springtribe.framework.jobslacker.DeployMode;
import org.springtribe.framework.jobslacker.EnableJobSlackerApi;

import com.github.paganini2008.devtools.io.FileUtils;

/**
 * 
 * JobSoupUIMain
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@EnableJobSlackerApi(DeployMode.UI)
@SpringBootApplication
@ComponentScan(basePackages = { "com.github.paganini2008.springworld.jobsoup.ui" })
public class JobSoupUIMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "springworld", "jobstorm", "ui");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("DEFAULT_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(JobSoupUIMain.class, args);
		System.out.println(Env.getPid());
	}

}
