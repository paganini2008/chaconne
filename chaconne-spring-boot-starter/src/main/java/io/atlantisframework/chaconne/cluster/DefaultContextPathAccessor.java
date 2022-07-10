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
package io.atlantisframework.chaconne.cluster;

import com.github.paganini2008.devtools.multithreads.AtomicLongSequence;

/**
 * 
 * DefaultContextPathAccessor
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class DefaultContextPathAccessor implements ContextPathSelector {

	private final AtomicLongSequence counter = new AtomicLongSequence();

	@Override
	public String selectContextPath(String clusterName, String[] contextPaths) {
		return contextPaths[(int) (counter.getAndIncrement() % contextPaths.length)];
	}

}
