package com.smoner.rpc.demo2.framework.exception;

/**
 * Created by smoner on 2017/1/29.
 */
public class ConnectorException  extends FrameworkRuntimeException {

    private static final long serialVersionUID = -6049910873194670737L;

    public ConnectorException() {
        super();
    }

    public ConnectorException(String msg) {
        super(msg);
    }

    public ConnectorException(String msg, Throwable exp) {
        super(msg, exp);
    }

}