package indi.atlantis.framework.chaconne.console;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.io.FileUtils;

import indi.atlantis.framework.chaconne.cluster.EnableChaconneClientMode;

/**
 * 
 * ChaconneConsoleMain
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
@EnableChaconneClientMode
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ChaconneConsoleMain {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "indi", "atlantis", "framework", "chaconne", "console");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("DEFAULT_LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(ChaconneConsoleMain.class, args);
		System.out.println(Env.getPid());
	}

}
