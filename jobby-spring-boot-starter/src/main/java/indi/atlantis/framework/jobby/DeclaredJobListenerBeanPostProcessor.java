package indi.atlantis.framework.jobby;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 
 * DeclaredJobListenerBeanPostProcessor
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class DeclaredJobListenerBeanPostProcessor implements BeanPostProcessor {

	@Autowired
	private LifeCycleListenerContainer lifeCycleListenerContainer;

	@Autowired
	private JobRuntimeListenerContainer jobRuntimeListenerContainer;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof LifeCycleListener) {
			LifeCycleListener listener = (LifeCycleListener) bean;
			lifeCycleListenerContainer.addListener(listener);
		}

		if (bean instanceof JobRuntimeListener) {
			JobRuntimeListener listener = (JobRuntimeListener) bean;
			JobKey jobKey = bean instanceof JobDefinition ? JobKey.of((JobDefinition) bean) : null;
			jobRuntimeListenerContainer.addListener(jobKey, listener);
		}
		return bean;
	}

}
