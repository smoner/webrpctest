package com.smoner.rpc.demo2.framework.exception;

/**
 * Created by smoner on 2017/1/27.
 */

public class ComponentNotFoundException extends ComponentException {

    private static final long serialVersionUID = -2036876653664356233L;

    public ComponentNotFoundException() {
        super();
    }

    public ComponentNotFoundException(String msg) {
        super(msg);
    }

    public ComponentNotFoundException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ComponentNotFoundException(String name, String msg) {
        super(name, msg);

    }

    public ComponentNotFoundException(String name, String msg, Throwable throwable) {
        super(name, msg, throwable);

    }

    public ComponentNotFoundException(String containerName, String name, String msg) {
        super(containerName, name, msg);

    }

    public ComponentNotFoundException(String containerName, String name, String msg, Throwable throwable) {
        super(containerName, name, msg, throwable);
    }

}