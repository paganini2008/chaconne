package com.github.chaconne.client;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.chaconne.ChaconneException;
import com.github.chaconne.cluster.utils.ApplicationContextUtils;
import com.github.chaconne.cluster.utils.ExceptionUtils;

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
            throw new ChaconneException(e.getMessage(), e);
        }
        try {
            Object taskBean = ApplicationContextUtils.getOrCreateBean(beanClass);
            Object result = MethodUtils.invokeMethod(taskBean, true, runTaskRequest.getTaskMethod(),
                    runTaskRequest.getInitialParameter());
            return ApiResponse.ok(result);
        } catch (Throwable e) {
            return ApiResponse.bad("Failed to run task: " + runTaskRequest,
                    ExceptionUtils.toArray(e));
        }
    }


}
