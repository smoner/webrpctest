package com.smoner.rpc.demo2.framework.exception;

import java.io.IOException;

/**
 * Created by smoner on 2017/1/29.
 */
public class ConnectorIOException  extends ConnectorException {

    private static final long serialVersionUID = 460583024109217841L;

    public ConnectorIOException() {
        super();
    }

    public ConnectorIOException(String msg) {
        super(msg);
    }

    public ConnectorIOException(String msg, IOException throwable) {
        super(msg, throwable);
    }

}