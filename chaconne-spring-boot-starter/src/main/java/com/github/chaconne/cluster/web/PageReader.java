package com.github.chaconne.cluster.web;

import java.util.List;

/**
 * 
 * @Description: PageReader
 * @Author: Fred Feng
 * @Date: 08/10/2024
 * @Version 1.0.0
 */
public interface PageReader<T> extends Countable {

    default List<T> list(int offset, int limit) throws Exception {
        return list(1, offset, limit);
    }

    List<T> list(int pageNumber, int offset, int limit) throws Exception;

    default PageResponse<T> list(PageRequest pageRequest) {
        return new SimplePageResponse<T>(pageRequest, this);
    }
}
