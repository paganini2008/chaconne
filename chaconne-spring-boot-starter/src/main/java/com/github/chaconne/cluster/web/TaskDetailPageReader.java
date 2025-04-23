package com.github.chaconne.cluster.web;

import java.util.Collections;
import java.util.List;
import com.github.chaconne.TaskDetail;
import com.github.chaconne.TaskManager;
import com.github.chaconne.cluster.TaskDetailVo;
import com.github.chaconne.cluster.TaskQueryDto;

/**
 * 
 * @Description: TaskDetailPageReader
 * @Author: Fred Feng
 * @Date: 24/04/2025
 * @Version 1.0.0
 */
public class TaskDetailPageReader implements PageReader<TaskDetailVo> {

    private final TaskManager taskManager;
    private final TaskQueryDto queryDto;

    public TaskDetailPageReader(TaskManager taskManager, TaskQueryDto queryDto) {
        this.taskManager = taskManager;
        this.queryDto = queryDto;
    }

    @Override
    public long rowCount() throws Exception {
        return taskManager.getTaskCount(queryDto.getTaskGroup(), queryDto.getTaskName(),
                queryDto.getTaskClass());
    }

    @Override
    public List<TaskDetailVo> list(int pageNumber, int offset, int limit) throws Exception {
        List<TaskDetail> list = taskManager.findTaskDetails(queryDto.getTaskGroup(),
                queryDto.getTaskName(), queryDto.getTaskClass(), limit, offset);
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        return list.stream().map(td -> new TaskDetailVo(td)).toList();
    }

}
