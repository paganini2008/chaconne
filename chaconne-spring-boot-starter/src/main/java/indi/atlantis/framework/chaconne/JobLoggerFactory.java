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

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.proxy.Aspect;
import com.github.paganini2008.devtools.proxy.JdkProxyFactory;
import com.github.paganini2008.devtools.proxy.ProxyFactory;

import lombok.Getter;

/**
 * 
 * JobLoggerFactory
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public final class JobLoggerFactory {

	private static final ProxyFactory proxyFactory = new JdkProxyFactory();
	private static final LruMap<JobKey, LoggerProxy> proxies = new LruMap<JobKey, LoggerProxy>(1024);

	public static Logger getLogger(Logger target, long traceId, JobKey jobKey, LogManager logManager) {
		LoggerProxy holder = proxies.get(jobKey);
		if (holder == null) {
			proxies.put(jobKey, createLoggerProxy(target, jobKey, logManager));
			holder = proxies.get(jobKey);
		}
		((JobLoggerAspect) holder.getAspect()).setTraceId(traceId);
		return holder.getProxy();
	}

	private static LoggerProxy createLoggerProxy(Logger target, JobKey jobKey, LogManager logManager) {
		Aspect aspect = new JobLoggerAspect(jobKey, logManager);
		Logger proxy = (Logger) proxyFactory.getProxy(target, aspect, Logger.class);
		return new LoggerProxy(proxy, aspect);
	}

	@Getter
	private static class LoggerProxy {

		private final Logger proxy;
		private final Aspect aspect;

		LoggerProxy(Logger proxy, Aspect aspect) {
			this.proxy = proxy;
			this.aspect = aspect;
		}

	}

}
