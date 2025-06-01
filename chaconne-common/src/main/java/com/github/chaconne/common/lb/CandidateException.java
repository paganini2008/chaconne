package com.github.chaconne.common.lb;

/**
 * 
 * @Description: CandidateException
 * @Author: Fred Feng
 * @Date: 25/05/2025
 * @Version 1.0.0
 */
public class CandidateException extends RuntimeException {

    private static final long serialVersionUID = 6651762182015119672L;

    public CandidateException() {
        super();
    }

    public CandidateException(String msg) {
        super(msg);
    }

    public CandidateException(String msg, Throwable e) {
        super(msg, e);
    }

}
