package com.github.chaconne.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @Description: TaskInfo
 * @Author: Fred Feng
 * @Date: 11/04/2025
 * @Version 1.0.0
 */
@Getter
@Setter
@ToString
public class TaskInfo {

    private String taskGroup;
    private String taskName;
    private String taskClass;
    private String taskMethod;
    private String description;
    private String cron;
    private int maxRetryCount;
    private long timeout;

}
