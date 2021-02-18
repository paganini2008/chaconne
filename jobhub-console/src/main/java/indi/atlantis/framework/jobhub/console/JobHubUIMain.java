package indi.atlantis.framework.jobhub.console;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.paganini2008.devtools.io.FileUtils;

import indi.atlantis.framework.jobhub.DeployMode;
import indi.atlantis.framework.jobhub.EnableJobHubApi;

/**
 * 
 * JobHubUIMain
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
@EnableJobHubApi(DeployMode.UI)
@SpringBootApplication
public class JobHubUIMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "indi", "atlantis", "framework", "jobhub", "ui");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("DEFAULT_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(JobHubUIMain.class, args);
		System.out.println(Env.getPid());
	}

}
