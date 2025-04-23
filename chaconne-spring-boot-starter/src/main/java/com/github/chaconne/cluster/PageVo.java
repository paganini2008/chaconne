package com.github.chaconne.cluster;

import java.util.List;

/**
 * 
 * @Description: PageVo
 * @Author: Fred Feng
 * @Date: 24/04/2025
 * @Version 1.0.0
 */
public class PageVo<T> {

    private List<T> content;

    private int pageNumber;

    private int pageSize;

    private long totalRecords;

    private boolean nextPage;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
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

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public boolean isNextPage() {
        return nextPage;
    }

    public void setNextPage(boolean nextPage) {
        this.nextPage = nextPage;
    }
}
