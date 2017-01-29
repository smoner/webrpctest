package com.smoner.rpc.demo2.framework.core;

import com.smoner.rpc.demo2.framework.exception.ComponentNotFoundException;

import javax.naming.Context;

public interface JndiContext {

    public Context getInitialContext();

    public Object lookup(String jndiName) throws ComponentNotFoundException;

    public Object lookupWithoutCache(String jndiName) throws ComponentNotFoundException;

}