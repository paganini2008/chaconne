package com.github.chaconne.cluster;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.chaconne.common.TaskMemberInstance;
import com.github.chaconne.common.TaskMemberRequest;

/**
 * 
 * @Description: TaskMemberManagerEndpoint
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
@RequestMapping("/chac")
@RestController
public class TaskMemberManagerEndpoint {

    @Autowired
    private TaskMemberManager taskMemberManager;

    @PostMapping("/register")
    public void register(@RequestBody TaskMemberRequest taskMemberRequest) {
        TaskMemberInstance taskMemberInstance = new TaskMemberInstance();
        BeanUtils.copyProperties(taskMemberRequest, taskMemberInstance);
        taskMemberManager.addTaskMember(taskMemberInstance);
    }

}
