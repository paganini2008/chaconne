package com.github.chaconne.cluster;

import com.github.chaconne.TaskInvocationException;

/**
 * 
 * @Description: RemoteTaskInvocationException
 * @Author: Fred Feng
 * @Date: 20/04/2025
 * @Version 1.0.0
 */
public class RemoteTaskInvocationException extends TaskInvocationException {

    private static final long serialVersionUID = -4972729566401123014L;

    public RemoteTaskInvocationException(String msg) {
        this(msg, new String[0]);
    }

    public RemoteTaskInvocationException(String msg, String[] details) {
        super(msg);
        this.details = details;
    }

    private final String[] details;

    public String[] getDetails() {
        return details;
    }

    @Override
    public synchronized Throwable getCause() {
        // TODO Auto-generated method stub
        return super.getCause();
    }



}
