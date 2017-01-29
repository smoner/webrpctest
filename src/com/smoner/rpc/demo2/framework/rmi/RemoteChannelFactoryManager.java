package com.smoner.rpc.demo2.framework.rmi;

/**
 * Created by smoner on 2017/1/29.
 */

public abstract class RemoteChannelFactoryManager {
    static RemoteChannelFactoryManager def;

    public static RemoteChannelFactoryManager getDefault() {
        if (def == null) {
            synchronized (RemoteChannelFactoryManager.class) {
                if (def == null) {
                    RemoteChannelFactoryManager rcfm = new RemoteChannelFactoryManagerImpl();
                    HttpRemoteChannelFactory rcf = new HttpRemoteChannelFactory();
                    rcfm.registerRemoteChannelFactory("http",
                            rcf);
                    rcfm.registerRemoteChannelFactory("https", rcf);
                    def = rcfm;
                }
            }
        }
        return def;
    }

    public static void setDefault(RemoteChannelFactoryManager def1) {
        def = def1;
    }

    public abstract RemoteChannelFactory getRemoteChannelFactory(String schema);

    public abstract void registerRemoteChannelFactory(String schema,
                                                      RemoteChannelFactory rcf);

    public abstract RemoteChannelFactory unregisterRemoteChannelFactory(
            String schema);
}
