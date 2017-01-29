package com.smoner.rpc.demo2.framework.rmi;

import com.smoner.rpc.demo2.framework.testclass.Address;

/**
 * Created by smoner on 2017/1/29.
 */
public interface RemoteChannelFactory {
    public RemoteChannel createRemoteChannel(Address url);
}
