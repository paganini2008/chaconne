package com.github.chaconne;

import com.github.chaconne.client.RunTaskRequest;

/**
 * 
 * @Description: TaskMethodRemoteCaller
 * @Author: Fred Feng
 * @Date: 16/04/2025
 * @Version 1.0.0
 */
public interface TaskMethodRemoteCaller {

    void call(RunTaskRequest runTaskRequest);

}
