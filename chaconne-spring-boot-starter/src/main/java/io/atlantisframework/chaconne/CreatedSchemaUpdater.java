/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package io.atlantisframework.chaconne;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.github.paganini2008.devtools.jdbc.ConnectionFactory;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * CreatedSchemaUpdater
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
@Slf4j
public class CreatedSchemaUpdater implements SchemaUpdater {

	private final ConnectionFactory connectionFactory;

	public CreatedSchemaUpdater(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@PostConstruct
	@Override
	public void onCluserOnline() throws Exception {
		Connection connection = null;
		try {
			connection = connectionFactory.getConnection();
			for (Map.Entry<String, String> entry : new HashMap<String, String>(DdlScripts.CreateScripts.ddls()).entrySet()) {
				if (!JdbcUtils.existsTable(connection, null, entry.getKey())) {
					JdbcUtils.update(connection, entry.getValue());
					log.info("Execute ddl: " + entry.getValue());
				}
			}
		} finally {
			JdbcUtils.closeQuietly(connection);
		}

	}

	@PreDestroy
	@Override
	public void onClusterOffline() {
	}

}
