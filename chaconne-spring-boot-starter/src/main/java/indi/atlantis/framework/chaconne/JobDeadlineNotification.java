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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.paganini2008.devtools.multithreads.Executable;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import indi.atlantis.framework.tridenter.utils.BeanLifeCycle;

/**
 * 
 * JobDeadlineNotification
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class JobDeadlineNotification extends JobConditionalTermination implements Executable, BeanLifeCycle {

	private final Map<JobKey, Date> deadlines = new ConcurrentHashMap<JobKey, Date>();
	private Timer timer;

	@Autowired
	private JobQueryDao jobQueryDao;

	@Override
	public void configure() throws Exception {
		refresh();
		this.timer = ThreadUtils.scheduleAtFixedRate(this, 1, TimeUnit.MINUTES);
	}

	@Override
	protected boolean apply(long traceId, JobKey jobKey, Object attachment, Date startDate) {
		Date theDeadline = deadlines.get(jobKey);
		if (theDeadline != null && theDeadline.before(startDate)) {
			deadlines.remove(jobKey);
			return true;
		}
		return false;
	}

	@Override
	public boolean execute() {
		refresh();
		return true;
	}

	private void refresh() {
		List<Map<String, Object>> dataList = jobQueryDao.selectJobTriggerDeadlines();
		JobKey jobKey;
		Date endDate;
		for (Map<String, Object> data : dataList) {
			jobKey = JobKey.of(data);
			endDate = (Date) data.get("endDate");
			deadlines.put(jobKey, endDate);
		}
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
		}
	}

}
