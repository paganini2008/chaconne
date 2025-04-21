package com.github.chaconne.cluster;

import com.github.chaconne.ChaconneException;

/**
 * 
 * @Description: TaskInvocationException
 * @Author: Fred Feng
 * @Date: 20/04/2025
 * @Version 1.0.0
 */
public class TaskInvocationException extends ChaconneException {

    private static final long serialVersionUID = 330511858530035307L;

    public TaskInvocationException(String msg) {
        super(msg);
    }

    public TaskInvocationException(String msg, Throwable e) {
        super(msg, e);
    }

}
