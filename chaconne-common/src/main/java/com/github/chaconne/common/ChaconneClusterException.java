package com.github.chaconne.common;

import org.springframework.core.NestedRuntimeException;

/**
 * 
 * @Description: ChaconneClusterException
 * @Author: Fred Feng
 * @Date: 01/06/2025
 * @Version 1.0.0
 */
public class ChaconneClusterException extends NestedRuntimeException {

    private static final long serialVersionUID = -2670845200714613141L;

    public ChaconneClusterException(String msg) {
        super(msg);
    }

    public ChaconneClusterException(String msg, Throwable cause) {
        super(msg, cause);
    }



}
