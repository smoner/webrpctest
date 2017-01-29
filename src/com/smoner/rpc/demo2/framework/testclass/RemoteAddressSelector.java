package com.smoner.rpc.demo2.framework.testclass;

import java.io.Serializable;

/**
 * Created by smoner on 2017/1/29.
 */
public interface RemoteAddressSelector  extends Serializable {

    /**
     * Select a remote address for remote invocation
     *
     */
    Address select() throws Exception;

    void fail(Address address);
}