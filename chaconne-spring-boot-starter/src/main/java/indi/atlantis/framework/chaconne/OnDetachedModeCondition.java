/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import indi.atlantis.framework.chaconne.cluster.DetachedMode;

/**
 * 
 * OnDetachedModeCondition
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class OnDetachedModeCondition extends SpringBootCondition {

	private static final ConditionMessage EMPYT_MESSAGE = ConditionMessage.empty();

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnDetachedMode.class.getName());
		DetachedMode detachedMode = (DetachedMode) annotationAttributes.get("value");
		final String role = context.getEnvironment().getRequiredProperty("atlantis.framework.chaconne.detachedMode");
		if (detachedMode.getRole().equals(role)) {
			return ConditionOutcome.match(EMPYT_MESSAGE);
		}
		return ConditionOutcome.noMatch(EMPYT_MESSAGE);
	}

}
