package com.github.chaconne;

/**
 * 
 * @Description: ChaconneException
 * @Author: Fred Feng
 * @Date: 16/04/2025
 * @Version 1.0.0
 */
public class ChaconneException extends RuntimeException {

    private static final long serialVersionUID = -8660771248670135685L;

    public ChaconneException() {
        super();
    }

    public ChaconneException(String msg) {
        super(msg);
    }

    public ChaconneException(String msg, Throwable e) {
        super(msg, e);
    }

}
