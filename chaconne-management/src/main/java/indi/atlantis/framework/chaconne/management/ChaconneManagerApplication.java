/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package indi.atlantis.framework.chaconne.management;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.github.paganini2008.devtools.Env;
import com.github.paganini2008.devtools.io.FileUtils;

import indi.atlantis.framework.chaconne.cluster.DetachedMode;
import indi.atlantis.framework.chaconne.cluster.EnableChaconneDetachedMode;

/**
 * 
 * ChaconneManagerApplication
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@EnableChaconneDetachedMode(DetachedMode.PRODUCER)
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ChaconneManagerApplication {

	static {
		System.setProperty("spring.devtools.restart.enabled", "false");
		File logDir = FileUtils.getFile(FileUtils.getUserDirectory(), "logs", "indi", "atlantis", "framework", "chaconne", "management");
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		System.setProperty("LOG_BASE", logDir.getAbsolutePath());
	}

	public static void main(String[] args) {
		SpringApplication.run(ChaconneManagerApplication.class, args);
		System.out.println(Env.getPid());
	}
}
