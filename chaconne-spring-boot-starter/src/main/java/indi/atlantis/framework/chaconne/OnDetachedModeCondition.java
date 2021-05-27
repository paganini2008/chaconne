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
