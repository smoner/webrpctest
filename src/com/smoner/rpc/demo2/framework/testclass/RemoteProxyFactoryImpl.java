package com.smoner.rpc.demo2.framework.testclass;

import java.lang.reflect.Proxy;

/**
 * Created by smoner on 2017/1/29.
 */
public class RemoteProxyFactoryImpl extends RemoteProxyFactory {

    public Object createRemoteProxy(ClassLoader loader, ComponentMetaVO metaVO,
                                    RemoteAddressSelector ras) {
        RemoteInvocationHandler rih = new RemoteInvocationHandler(metaVO, ras);

        if (loader == null) {
            loader = RemoteProxyFactoryImpl.class.getClassLoader();
        }

        Class<?>[] apis = null;
        try {
            if (metaVO.getInterfaces() != null) {
                String[] interfaces = metaVO.getInterfaces();
                apis = new Class[interfaces.length + 1];
                for (int i = 0; i < interfaces.length; i++) {
                    apis[i] = Class.forName(interfaces[i], false, loader);
                }
                apis[interfaces.length] = RemoteProxy.class;
            }
        } catch (ClassNotFoundException cnfe) {
            System.out.print(cnfe.getMessage());
        }
        Object o = Proxy.newProxyInstance(loader, apis, rih);
        return o;
    }
}