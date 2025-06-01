package com.github.chaconne.cluster;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.chaconne.CustomTask;
import com.github.chaconne.CustomTaskFactory;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskId;
import com.github.chaconne.TaskManager;
import com.github.chaconne.TimeWheelScheduler;
import com.github.chaconne.cluster.web.PageRequest;
import com.github.chaconne.cluster.web.PageResponse;
import com.github.chaconne.cluster.web.PageVo;
import com.github.chaconne.cluster.web.TaskDetailPageReader;
import com.github.chaconne.common.ApiResponse;
import com.github.chaconne.common.CreateTaskRequest;
import com.github.chaconne.common.TaskIdRequest;
import com.github.chaconne.common.utils.BeanUtils;

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
    private TimeWheelScheduler clockWheelScheduler;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private CustomTaskFactory customTaskFactory;

    @PostMapping("/save-task")
    public ResponseEntity<ApiResponse<Boolean>> saveTask(
            @RequestBody CreateTaskRequest requestBody) {
        Map<String, Object> beanMap = BeanUtils.bean2Map(requestBody);
        CustomTask customTask = customTaskFactory.createTaskObject(beanMap);
        if (taskManager.hasTask(customTask.getTaskId())) {
            return ResponseEntity.ok(ApiResponse.ok(false));
        } else {
            taskManager.saveTask(customTask, requestBody.getInitialParameter());
            return ResponseEntity.ok(ApiResponse.ok(true));
        }
    }

    @PostMapping("/schedule-task")
    public ResponseEntity<ApiResponse<Boolean>> scheduleTask(
            @RequestBody TaskIdRequest requestBody) {
        boolean flag = clockWheelScheduler
                .schedule(TaskId.of(requestBody.getTaskGroup(), requestBody.getTaskName()));
        return ResponseEntity.ok(ApiResponse.ok(flag));
    }

    @PostMapping("/exist-task")
    public ResponseEntity<ApiResponse<Boolean>> hasTask(@RequestBody TaskIdRequest requestBody) {
        boolean flag = taskManager
                .hasTask(TaskId.of(requestBody.getTaskGroup(), requestBody.getTaskName()));
        return ResponseEntity.ok(ApiResponse.ok(flag));
    }

    @DeleteMapping("/remove-task")
    public ResponseEntity<ApiResponse<TaskDetailVo>> removeTask(
            @RequestBody TaskIdRequest requestBody) {
        TaskDetail taskDetail = taskManager
                .removeTask(TaskId.of(requestBody.getTaskGroup(), requestBody.getTaskName()));
        return ResponseEntity.ok(ApiResponse.ok(new TaskDetailVo(taskDetail)));
    }

    @PostMapping("/query-task")
    public ApiResponse<PageVo<TaskDetailVo>> queryForTask(@RequestBody TaskQueryDto queryDto) {
        TaskDetailPageReader pageReader = new TaskDetailPageReader(taskManager, queryDto);
        PageResponse<TaskDetailVo> pageResponse =
                pageReader.list(PageRequest.of(queryDto.getPageNumber(), queryDto.getPageSize()));
        PageVo<TaskDetailVo> pageVo = new PageVo<TaskDetailVo>();
        pageVo.setContent(pageResponse.getContent());
        pageVo.setPageNumber(pageResponse.getPageNumber());
        pageVo.setPageSize(pageResponse.getPageSize());
        pageVo.setTotalRecords(pageResponse.getTotalRecords());
        pageVo.setNextPage(pageResponse.hasNextPage());
        return ApiResponse.ok(pageVo);
    }



}
