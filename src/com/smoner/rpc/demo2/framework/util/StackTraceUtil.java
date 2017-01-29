package com.smoner.rpc.demo2.framework.util;


import com.smoner.rpc.demo2.framework.exception.FrameworkException;
import com.smoner.rpc.demo2.framework.exception.FrameworkRuntimeException;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;


/**
 * @author 何冠宇
 *
 *         Date: 2006-7-17 Time: 16:38:13
 */
public class StackTraceUtil {

    private static final String NC_TOP_CHAIN = "javax.servlet.GenericServlet";

    private static final String NC_TOP_CHAIN1 = "javax.servlet.http.HttpServlet";

    private static final String NC_TOP_HOOK = "nc.bs.framework.server.WebApplicationStartupHook";

    private static final boolean ADJUST_EXCEPTION = "true".equals(System
            .getProperty("nc.adjustException", "true"));

    public static StackTraceElement[] translateStackTrace(
            StackTraceElement[] stes) {
        if (ADJUST_EXCEPTION) {
            int i = 0;
            for (; i < stes.length; i++) {
                if (NC_TOP_CHAIN.equals(stes[i].getClassName())
                        || NC_TOP_CHAIN1.equals(stes[i].getClassName())) {
                    break;
                }

                if (NC_TOP_HOOK.equals(stes[i].getClassName())) {
                    i++;
                    break;
                }
            }
            if (i != stes.length && i != 0) {
                StackTraceElement[] nstes = new StackTraceElement[i];
                for (int j = 0; j < i; j++) {
                    nstes[j] = stes[j];
                }
                stes = nstes;

            }
        }

        return stes;
    }

    public static void printStackTrace(PrintStream s, Throwable thr) {
        synchronized (s) {
            s.println(thr);
            StackTraceElement[] trace = thr.getStackTrace();

            if (!(thr instanceof FrameworkException || thr instanceof FrameworkRuntimeException)) {
                trace = translateStackTrace(trace);
            }

            for (int i = 0; i < trace.length; i++)
                s.println("\tat " + trace[i]);

            Throwable ourCause = thr.getCause();
            if (ourCause != null)
                printStackTraceAsCause(s, ourCause, trace);
        }
    }

    private static void printStackTraceAsCause(PrintStream s, Throwable cause,
                                               StackTraceElement[] causedTrace) {
        // assert Thread.holdsLock(s);

        // Compute number of frames in common between this and caused
        StackTraceElement[] trace = cause.getStackTrace();

        if (!(cause instanceof FrameworkException || cause instanceof FrameworkRuntimeException)) {
            trace = translateStackTrace(trace);
        }

        int m = trace.length - 1, n = causedTrace.length - 1;
        while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
            m--;
            n--;
        }
        int framesInCommon = trace.length - 1 - m;

        s.println("Caused by: " + cause);
        for (int i = 0; i <= m; i++)
            s.println("\tat " + trace[i]);
        if (framesInCommon != 0)
            s.println("\t... " + framesInCommon + " more");

        // Recurse if we have a cause
        Throwable ourCause = cause.getCause();
        if (ourCause != null)
            printStackTraceAsCause(s, ourCause, trace);
    }

    public static void printStackTrace(PrintWriter s, Throwable thr) {
        HashSet<Throwable> causes = new HashSet<Throwable>();
        synchronized (s) {
            s.println(thr);
            StackTraceElement[] trace = thr.getStackTrace();

            if (!(thr instanceof FrameworkException || thr instanceof FrameworkRuntimeException)) {
                trace = translateStackTrace(trace);
            }

            for (int i = 0; i < trace.length; i++)
                s.println("\tat " + trace[i]);

            Throwable ourCause = thr.getCause();
            if (ourCause != null)
                printStackTraceAsCause(causes, s, ourCause, trace);
        }
    }

    private static void printStackTraceAsCause(Set<Throwable> causes,
                                               PrintWriter s, Throwable cause, StackTraceElement[] causedTrace) {

        causes.add(cause);
        StackTraceElement[] trace = cause.getStackTrace();
        if (!(cause instanceof FrameworkException || cause instanceof FrameworkRuntimeException)) {
            trace = translateStackTrace(trace);
        }

        int m = trace.length - 1, n = causedTrace.length - 1;
        while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
            m--;
            n--;
        }
        int framesInCommon = trace.length - 1 - m;

        s.println("Caused by: " + cause);
        for (int i = 0; i <= m; i++)
            s.println("\tat " + trace[i]);
        if (framesInCommon != 0)
            s.println("\t... " + framesInCommon + " more");

        // Recurse if we have a cause
        Throwable ourCause = cause.getCause();
        if (ourCause != null)
            if (!causes.contains(ourCause)) {
                printStackTraceAsCause(causes, s, ourCause, trace);
            } else {
            }
    }
}
