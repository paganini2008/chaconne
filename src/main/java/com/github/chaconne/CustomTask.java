package com.github.chaconne;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.github.cronsmith.CRON;
import com.github.cronsmith.cron.CronExpression;
import com.github.cronsmith.scheduler.CronTaskException;

/**
 * 
 * @Description: CustomTask
 * @Author: Fred Feng
 * @Date: 08/04/2025
 * @Version 1.0.0
 */
public class CustomTask implements Task {

    public CustomTask(Map<String, Object> info) {
        this(info, new DefaultTaskMethodInvocation());
    }

    public CustomTask(Map<String, Object> info, TaskMethodInvocation taskMethodInvocation) {
        this.info = info;
        this.taskMethodInvocation = taskMethodInvocation;
    }

    private final Map<String, Object> info;
    private final TaskMethodInvocation taskMethodInvocation;

    @Override
    public TaskId getTaskId() {
        return TaskId.of((String) info.getOrDefault("taskGroup", "default"),
                (String) info.get("taskName"));
    }

    @Override
    public String getDescription() {
        return (String) info.get("description");
    }

    @Override
    public long getTimeout() {
        return (Long) info.getOrDefault("timeout", -1L);
    }

    @Override
    public int getMaxRetryCount() {
        return (Integer) info.getOrDefault("maxRetryCount", -1);
    }

    @Override
    public CronExpression getCronExpression() {
        Object object = info.get("cronExpression");
        if (object instanceof CronExpression) {
            return (CronExpression) object;
        } else if (object instanceof CharSequence) {
            return CRON.parse(object.toString());
        } else if (object instanceof byte[]) {
            return CronExpression.deserialize((byte[]) object);
        } else if (object instanceof LocalDateTime) {
            return CRON.atFuture((LocalDateTime) object);
        } else if (object instanceof LocalDate) {
            return CRON.atFuture((LocalDate) object);
        } else if (object instanceof LocalTime) {
            return CRON.setInterval((LocalTime) object);
        }
        throw new CronTaskException("CronExpression is required");
    }

    @Override
    public Object execute(String initialParameter) {
        String taskClassName = (String) info.get("taskClass");
        String taskMethodName = (String) info.getOrDefault("taskMethod", "execute");
        return taskMethodInvocation.invokeTaskMethod(getTaskId(), taskClassName, taskMethodName,
                initialParameter);
    }

    private static class DefaultTaskMethodInvocation implements TaskMethodInvocation {

        private static final Map<TaskId, Function<String, Object>> invocations = new HashMap<>();

        @Override
        public Object invokeTaskMethod(TaskId taskId, String taskClassName, String taskMethodName,
                String initialParameter) {
            Function<String, Object> f = invocations.get(taskId);
            if (f == null) {
                f = invocations.putIfAbsent(taskId,
                        new InvocationFunction(taskClassName, taskMethodName));
                f = invocations.get(taskId);
            }
            return f.apply(initialParameter);
        }


    }

    private static class InvocationFunction implements Function<String, Object> {

        private final Object taskInstance;
        private final Method taskMethod;

        InvocationFunction(String taskClassName, String taskMethodName) {
            try {
                Class<?> clz = Class.forName(taskClassName, false,
                        Thread.currentThread().getContextClassLoader());
                taskInstance = clz.newInstance();
                taskMethod = clz.getDeclaredMethod(taskMethodName, String.class);
                taskMethod.setAccessible(true);
            } catch (Exception e) {
                throw new CronTaskException(e.getMessage(), e);
            }
        }

        @Override
        public Object apply(String initialParameter) {
            try {
                return taskMethod.invoke(taskInstance, initialParameter);
            } catch (InvocationTargetException e) {
                throw new CronTaskException(e.getMessage(), e.getTargetException());
            } catch (Exception e) {
                throw new CronTaskException(e.getMessage(), e);
            }
        }

    }

}
