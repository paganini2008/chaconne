package com.github.chaconne.cluster;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.chaconne.CustomTaskImpl;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskId;
import com.github.chaconne.TaskInvocation;
import com.github.chaconne.TaskManager;
import com.github.chaconne.client.ApiResponse;
import com.github.chaconne.client.CreateTaskRequest;
import com.github.chaconne.client.TaskIdRequest;
import com.github.chaconne.cluster.utils.BeanUtils;

/**
 * 
 * @Description: TaskManagerEndpoint
 * @Author: Fred Feng
 * @Date: 11/04/2025
 * @Version 1.0.0
 */
@RequestMapping("/chac")
@RestController
public class TaskManagerEndpoint {

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private TaskInvocation taskInvocation;

    @PostMapping("/save-task")
    public ApiResponse<TaskDetailVo> saveTask(@RequestBody CreateTaskRequest requestBody) {
        Map<String, Object> beanMap = BeanUtils.bean2Map(requestBody);
        CustomTaskImpl customTask = new CustomTaskImpl(beanMap, taskInvocation);
        boolean saved = true;
        if (requestBody.getUpdatePolicy() != null) {
            switch (requestBody.getUpdatePolicy()) {
                case CREATE:
                    if (taskManager.hasTask(customTask.getTaskId())) {
                        saved = false;
                    }
                    break;
                case REBUILD:
                    TaskDetail taskDetail = taskManager.removeTask(customTask.getTaskId());
                    saved = taskDetail != null;
                    break;
                case MERGE:
                    break;
                case NONE:
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unexpected value: " + requestBody.getUpdatePolicy());
            }
        }
        if (saved) {
            TaskDetail taskDetail =
                    taskManager.saveTask(customTask, requestBody.getInitialParameter());
            return ApiResponse.ok(new TaskDetailVo(taskDetail));
        } else {
            return ApiResponse
                    .ok(new TaskDetailVo(taskManager.getTaskDetail(customTask.getTaskId())));
        }
    }

    @PostMapping("/exist-task")
    public ApiResponse<Boolean> hasTask(@RequestBody TaskIdRequest requestBody) {
        boolean flag = taskManager
                .hasTask(TaskId.of(requestBody.getTaskGroup(), requestBody.getTaskName()));
        return ApiResponse.ok(flag);
    }

    @DeleteMapping("/remove-task")
    public ApiResponse<TaskDetailVo> removeTask(@RequestBody TaskIdRequest requestBody) {
        TaskDetail taskDetail = taskManager
                .removeTask(TaskId.of(requestBody.getTaskGroup(), requestBody.getTaskName()));
        return ApiResponse.ok(new TaskDetailVo(taskDetail));
    }


}
