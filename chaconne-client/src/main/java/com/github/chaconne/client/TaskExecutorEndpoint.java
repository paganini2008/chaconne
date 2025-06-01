package com.github.chaconne.client;

import java.lang.reflect.Method;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.chaconne.common.ApiResponse;
import com.github.chaconne.common.ApplicationContextUtils;
import com.github.chaconne.common.ChaconneClusterException;
import com.github.chaconne.common.RunTaskRequest;
import com.github.chaconne.common.utils.ExceptionUtils;

/**
 * 
 * @Description: TaskExecutionEndpoint
 * @Author: Fred Feng
 * @Date: 11/04/2025
 * @Version 1.0.0
 */
@RequestMapping("/chac")
@RestController
public class TaskExecutorEndpoint {

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("pong");
    }

    @PostMapping("/run-task")
    public ApiResponse<Object> runTask(@RequestBody RunTaskRequest runTaskRequest)
            throws Throwable {
        Class<?> beanClass;
        try {
            beanClass = ClassUtils.forName(runTaskRequest.getTaskClass(), null);
        } catch (ClassNotFoundException e) {
            throw new ChaconneClusterException(e.getMessage(), e);
        }
        try {
            Object result = null;
            Object taskBean = ApplicationContextUtils.getOrCreateBean(beanClass);
            Method method = MethodUtils.getMatchingMethod(beanClass, runTaskRequest.getTaskMethod(),
                    String.class);
            if (method != null) {
                method.setAccessible(true);
                result = method.invoke(taskBean, runTaskRequest.getInitialParameter());
            } else {
                method = MethodUtils.getMatchingMethod(beanClass, runTaskRequest.getTaskMethod());
                method.setAccessible(true);
                result = method.invoke(taskBean);
            }
            return ApiResponse.ok(result);
        } catch (Throwable e) {
            return ApiResponse.bad("Failed to run task: " + runTaskRequest,
                    ExceptionUtils.toArray(e));
        }
    }


}
