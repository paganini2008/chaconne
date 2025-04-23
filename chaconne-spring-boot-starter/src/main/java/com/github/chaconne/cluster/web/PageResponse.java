package com.github.chaconne.cluster.web;

import java.util.List;

/**
 * 
 * @Description: PageResponse
 * @Author: Fred Feng
 * @Date: 08/03/2023
 * @Version 1.0.0
 */
public interface PageResponse<T> extends Iterable<EachPage<T>> {

    boolean isEmpty();

    boolean isLastPage();

    boolean isFirstPage();

    boolean hasNextPage();

    boolean hasPreviousPage();

    int getTotalPages();

    long getTotalRecords();

    int getOffset();

    int getPageSize();

    int getPageNumber();

    <E extends Exception> List<T> getContent() throws E;

    PageResponse<T> setPage(int pageNumber);

    PageResponse<T> lastPage();

    PageResponse<T> firstPage();

    PageResponse<T> nextPage();

    PageResponse<T> previousPage();

}
