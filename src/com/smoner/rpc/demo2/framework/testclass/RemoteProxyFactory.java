package com.smoner.rpc.demo2.framework.testclass;

/**
 * Created by smoner on 2017/1/29.
 */
public abstract class RemoteProxyFactory  {

    private static RemoteProxyFactory def;

    public static RemoteProxyFactory getDefault() {
        if (def == null) {
            synchronized (RemoteProxyFactory.class) {
                if (def == null) {
                    def = new RemoteProxyFactoryImpl();
                }
            }
        }
        return def;
    }

    public static void setDefault(RemoteProxyFactory rpf) {
        def = rpf;
    }

    public abstract Object createRemoteProxy(ClassLoader loader,
                                             ComponentMetaVO metaVO, RemoteAddressSelector ras);
}
