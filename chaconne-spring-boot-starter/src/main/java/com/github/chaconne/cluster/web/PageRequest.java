package com.github.chaconne.cluster.web;

/**
 * 
 * @Description: PageRequest in Generic paging tools
 * @Author: Fred Feng
 * @Date: 08/03/2023
 * @Version 1.0.0
 */
public interface PageRequest {

    int getPageNumber();

    int getPageSize();

    int getOffset();

    PageRequest next();

    PageRequest previous();

    PageRequest first();

    PageRequest set(int page);

    static PageRequest of(int pageSize) {
        return of(1, pageSize);
    }

    static PageRequest of(int pageNumber, int pageSize) {
        return new SimplePageRequest(pageNumber, pageSize);
    }
}
