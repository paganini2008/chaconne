package indi.atlantis.framework.jobhub.server;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.io.FileUtils;

import indi.atlantis.framework.jobhub.DeployMode;
import indi.atlantis.framework.jobhub.EnableJobHubApi;

/**
 * 
 * JobHubServerMain
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@EnableJobHubApi(value = DeployMode.SERVER, serverMode = ServerMode.PRODUCER)
@SpringBootApplication
public class JobHubServerMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "indi", "atlantis", "framework", "jobhub", "server");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(JobHubServerMain.class, args);
		System.out.println(Env.getPid());
	}
}
