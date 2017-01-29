package com.smoner.rpc.demo2.framework.exception;

import java.io.IOException;

/**
 * Created by smoner on 2017/1/29.
 */
public class ConnectorFailException extends ConnectorIOException {

    public ConnectorFailException() {
        super();
    }

    public ConnectorFailException(String msg) {
        super(msg);
    }

    public ConnectorFailException(String msg, IOException throwable) {
        super(msg, throwable);
    }

}
