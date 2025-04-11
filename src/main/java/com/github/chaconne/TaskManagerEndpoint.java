package com.github.chaconne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.chaconne.client.TaskInfo;

/**
 * 
 * @Description: TaskManagerEndpoint
 * @Author: Fred Feng
 * @Date: 11/04/2025
 * @Version 1.0.0
 */
@RequestMapping("/tm")
@RestController
public class TaskManagerEndpoint {

    @Autowired
    private TaskManager taskManager;

    @PostMapping("/save-task")
    public void saveTask(@RequestBody TaskInfo taskInfo) {

    }


}
