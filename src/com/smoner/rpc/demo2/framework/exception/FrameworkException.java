package com.smoner.rpc.demo2.framework.exception;


import com.smoner.rpc.demo2.framework.util.StackTraceUtil;

import java.io.PrintStream;
import java.io.PrintWriter;


/**
 *
 * The base exception of the framework
 */
public class FrameworkException extends Exception {

    private static final long serialVersionUID = -7817444337191584676L;

    private volatile boolean notAdjusted = true;

    public FrameworkException() {
        super();
    }

    public FrameworkException(String msg) {
        super(msg);
    }

    public FrameworkException(String msg, Throwable throwable) {
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