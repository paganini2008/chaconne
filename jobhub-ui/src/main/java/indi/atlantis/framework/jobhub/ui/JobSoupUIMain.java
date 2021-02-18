package indi.atlantis.framework.jobhub.ui;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.github.paganini2008.devtools.io.FileUtils;

import indi.atlantis.framework.jobhub.DeployMode;
import indi.atlantis.framework.jobhub.EnableJobHubApi;

/**
 * 
 * JobSoupUIMain
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@EnableJobHubApi(DeployMode.UI)
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
