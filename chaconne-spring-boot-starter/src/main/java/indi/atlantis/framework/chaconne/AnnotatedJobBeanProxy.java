package indi.atlantis.framework.chaconne;

import org.slf4j.Logger;

import com.github.paganini2008.devtools.reflection.MethodUtils;

import indi.atlantis.framework.chaconne.annotations.OnFailure;
import indi.atlantis.framework.chaconne.annotations.OnSuccess;
import indi.atlantis.framework.chaconne.annotations.Run;

/**
 * 
 * AnnotatedJobBeanProxy
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public class AnnotatedJobBeanProxy implements NotManagedJob {

	private final Object targetBean;

	AnnotatedJobBeanProxy(Object targetBean) {
		this.targetBean = targetBean;
	}

	@Override
	public Object execute(JobKey jobKey, Object attachment, Logger log) throws Exception {
		return MethodUtils.invokeMethodWithAnnotation(targetBean, Run.class, jobKey, attachment, log);
	}

	@Override
	public void onSuccess(JobKey jobKey, Object result, Logger log) {
		MethodUtils.invokeMethodsWithAnnotation(targetBean, OnSuccess.class, jobKey, result, log);
	}

	@Override
	public void onFailure(JobKey jobKey, Throwable e, Logger log) {
		MethodUtils.invokeMethodsWithAnnotation(targetBean, OnFailure.class, jobKey, e, log);
	}

}
