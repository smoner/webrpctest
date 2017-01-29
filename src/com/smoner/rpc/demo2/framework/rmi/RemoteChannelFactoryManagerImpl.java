package com.smoner.rpc.demo2.framework.rmi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by smoner on 2017/1/29.
 */
public class RemoteChannelFactoryManagerImpl  extends
        RemoteChannelFactoryManager {
    private Map<String, RemoteChannelFactory> factoryMap;

    public RemoteChannelFactoryManagerImpl() {
        factoryMap = new HashMap<String, RemoteChannelFactory>();
    }

    public RemoteChannelFactory getRemoteChannelFactory(String schema) {
        return factoryMap.get(schema);
    }

    public void registerRemoteChannelFactory(String schema,
                                             RemoteChannelFactory rcf) {
        factoryMap.put(schema, rcf);
    }

    public RemoteChannelFactory unregisterRemoteChannelFactory(String schema) {
        return factoryMap.remove(schema);
    }

}
