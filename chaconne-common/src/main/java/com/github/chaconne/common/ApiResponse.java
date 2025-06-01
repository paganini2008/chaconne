package com.github.chaconne.common;

import java.io.Serializable;

/**
 * 
 * @Description: ApiResponse
 * @Author: Fred Feng
 * @Date: 13/04/2025
 * @Version 1.0.0
 */
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer status;
    private String msg;
    private String[] errorDetails;
    private T data;

    public ApiResponse() {}

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String[] getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String[] errorDetails) {
        this.errorDetails = errorDetails;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok(null, data);
    }

    public static <T> ApiResponse<T> ok(String msg, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.setMsg(msg);
        apiResponse.setData(data);
        apiResponse.setStatus(1);
        return apiResponse;
    }

    public static <T> ApiResponse<T> bad(String msg, String[] errorDetails) {
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.setMsg(msg);
        apiResponse.setErrorDetails(errorDetails);
        apiResponse.setStatus(0);
        return apiResponse;
    }

}
