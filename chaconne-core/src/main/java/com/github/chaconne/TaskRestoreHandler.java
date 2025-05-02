package com.github.chaconne;

import java.time.LocalDateTime;

/**
 * 
 * @Description: TaskRestoreHandler
 * @Author: Fred Feng
 * @Date: 30/04/2025
 * @Version 1.0.0
 */
@FunctionalInterface
public interface TaskRestoreHandler {

    void onRestore(TaskId taskId, LocalDateTime nextFiredDateTime);

}
