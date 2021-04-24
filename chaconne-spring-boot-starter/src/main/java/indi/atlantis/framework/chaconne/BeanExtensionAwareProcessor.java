package indi.atlantis.framework.chaconne;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.github.paganini2008.devtools.beans.BeanInstantiationException;

/**
 * 
 * BeanExtensionAwareProcessor
 * 
 * @author Jimmy Hoff
 *
 * @since 1.0
 */
public class BeanExtensionAwareProcessor implements BeanPostProcessor {

	@Autowired
	private LifeCycleListenerContainer lifeCycleListenerContainer;

	@Autowired
	private JobListenerContainer jobListenerContainer;

	@Autowired
	private JobManager jobManager;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Job) {
			Job jobBean = (Job) bean;
			try {
				jobManager.persistJob(jobBean, null);
			} catch (Exception e) {
				throw new BeanInstantiationException(e);
			}
		}
		if (bean instanceof LifeCycleListener) {
			LifeCycleListener listener = (LifeCycleListener) bean;
			lifeCycleListenerContainer.addListener(listener);
		}
		if (bean instanceof JobListener) {
			JobListener listener = (JobListener) bean;
			JobKey jobKey = bean instanceof JobDefinition ? JobKey.of((JobDefinition) bean) : null;
			jobListenerContainer.addListener(jobKey, listener);
		}
		return bean;
	}

}
