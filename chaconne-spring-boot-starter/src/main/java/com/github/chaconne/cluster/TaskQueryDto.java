package com.github.chaconne.cluster;

/**
 * 
 * @Description: TaskQueryDto
 * @Author: Fred Feng
 * @Date: 24/04/2025
 * @Version 1.0.0
 */
public class TaskQueryDto {

    private String taskGroup;
    private String taskName;
    private String taskClass;
    private int pageNumber;
    private int pageSize;

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
