package com.github.chaconne.common.lb;

/**
 * 
 * @Description: NoAvailableCandidateException
 * @Author: Fred Feng
 * @Date: 19/04/2025
 * @Version 1.0.0
 */
public class NoAvailableCandidateException extends CandidateException {

    private static final long serialVersionUID = -5387192088491531307L;

    public NoAvailableCandidateException() {
        super();
    }

    public NoAvailableCandidateException(String msg) {
        super(msg);
    }
}
