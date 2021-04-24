package indi.atlantis.framework.chaconne.scheduler;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.io.FileUtils;

import indi.atlantis.framework.chaconne.cluster.DetachedMode;
import indi.atlantis.framework.chaconne.cluster.EnableChaconneDetachedMode;

/**
 * 
 * ChaconneSchedulerMain
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
@EnableChaconneDetachedMode(DetachedMode.PRODUCER)
@SpringBootApplication
public class ChaconneSchedulerMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "indi", "atlantis", "framework", "chaconne", "scheduler");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(ChaconneSchedulerMain.class, args);
		System.out.println(Env.getPid());
	}
}
