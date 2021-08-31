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
package indi.atlantis.framework.chaconne;

import org.slf4j.Logger;

import com.github.paganini2008.devtools.SystemPropertyUtils;
import com.github.paganini2008.devtools.TableView;

/**
 * 
 * Banner
 * 
 * @author Fred Feng
 *
 * @since 2.0.1
 */
public abstract class Banner {

	public static final String PRODUCT_NAME = "Chaconne";

	public static final String PRODUCT_VERSION = SystemPropertyUtils.getString("chaconne.version", "1.0.0");

	public static final String PRODUCT_DESCRIPTION = "To Build High Available Distributed Job Scheduling System";

	public static void printBanner(String deployMode, Logger logger) {
		TableView tableView = new TableView(6, 2);
		tableView.setWidth(0, 30);
		tableView.setWidth(1, 70);
		tableView.setValueOnRight(1, 0, "[my name]: ", 0);
		tableView.setValueOnLeft(1, 1, PRODUCT_NAME, 0);
		tableView.setValueOnRight(2, 0, "[my description]: ", 0);
		tableView.setValueOnLeft(2, 1, PRODUCT_DESCRIPTION, 0);
		tableView.setValueOnRight(3, 0, "[my version]: ", 0);
		tableView.setValueOnLeft(3, 1, PRODUCT_VERSION, 0);
		tableView.setValueOnRight(4, 0, "[current deploy mode]: ", 0);
		tableView.setValueOnLeft(4, 1, deployMode, 0);
		String[] lines = tableView.toStringArray(false, false);
		for (String line : lines) {
			logger.info(line);
		}
	}

}
