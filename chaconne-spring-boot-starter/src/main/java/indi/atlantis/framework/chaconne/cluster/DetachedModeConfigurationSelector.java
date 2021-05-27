package indi.atlantis.framework.chaconne.cluster;

import java.util.Collections;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 
 * DetachedModeConfigurationSelector
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class DetachedModeConfigurationSelector implements ImportSelector, EnvironmentAware {

	private Environment environment;

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		AnnotationAttributes annotationAttributes = AnnotationAttributes
				.fromMap(importingClassMetadata.getAnnotationAttributes(EnableChaconneDetachedMode.class.getName()));
		DetachedMode detachedMode = annotationAttributes.getEnum("value");
		((ConfigurableEnvironment) environment).getPropertySources().addLast(new MapPropertySource("jobhub:config:detachedMode",
				Collections.singletonMap("atlantis.framework.chaconne.detachedMode", detachedMode.getRole())));
		return new String[] { DetachedModeConfiguration.class.getName() };
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}
