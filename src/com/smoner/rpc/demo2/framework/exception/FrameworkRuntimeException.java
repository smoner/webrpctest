package com.smoner.rpc.demo2.framework.exception;


import com.smoner.rpc.demo2.framework.util.StackTraceUtil;

import java.io.PrintStream;
import java.io.PrintWriter;


/**
 *
 * The base runtime exception of the framework
 */
public class FrameworkRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -2373921480522454843L;

    private volatile boolean notAdjusted = true;

    public FrameworkRuntimeException() {
        super();
    }

    public FrameworkRuntimeException(String msg) {
        super(msg);
    }

    public FrameworkRuntimeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public StackTraceElement[] getStackTrace() {
        StackTraceElement stes[] = super.getStackTrace();

        if (notAdjusted) {
            notAdjusted = false;
            stes = StackTraceUtil.translateStackTrace(stes);
            super.setStackTrace(stes);

        }

        return stes;
    }

    public void printStackTrace(PrintWriter s) {
        StackTraceUtil.printStackTrace(s, this);

    }

    public void printStackTrace(PrintStream s) {
        StackTraceUtil.printStackTrace(s, this);
    }
}