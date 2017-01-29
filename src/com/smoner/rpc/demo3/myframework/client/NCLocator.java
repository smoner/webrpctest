package com.smoner.rpc.demo3.myframework.client;

import com.smoner.rpc.demo3.test.ILoginService;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smoner on 2017/1/29.
 */
public class NCLocator {
    public static final String DISPATCH_URL="http://127.0.0.1:18080/webrpctest/ServiceDispatcherServlet";
    private Map<String, Object> proxyMap = new HashMap<String, Object>();

    public <T> T lookup(Class<T> clazz) {
        return (T) lookup(clazz.getName());
    }

    public Object lookup(String name) {
        Object so = proxyMap.get(name);
        if (so != null) {
            return so;
        }
        ComponentMetaVO metaVO = new ComponentMetaVO();
        String[] interfaces = new String[]{ILoginService.class.getName()};
        metaVO.setInterfaces(interfaces);
        metaVO.setAlias(interfaces);
        metaVO.setName(ILoginService.class.getName());
        so = proxyMap.get(metaVO.getName());
        if (so != null) {
            return so;
        }
        Address address = null;
        try {
            address = new Address(DISPATCH_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        so = createRemoteProxy(NCLocator.class.getClassLoader(), metaVO, address);
        proxyMap.put(metaVO.getName(), so);
        return so;
    }
    public Object createRemoteProxy(ClassLoader loader, ComponentMetaVO metaVO,
                                    Address address) {
        RemoteInvocationHandler rih = new RemoteInvocationHandler(metaVO, address);
        Class<?>[] apis = null;
        try {
            if (metaVO.getInterfaces() != null) {
                String[] interfaces = metaVO.getInterfaces();
                apis = new Class[interfaces.length ];
                for (int i = 0; i < interfaces.length; i++) {
                    apis[i] = Class.forName(interfaces[i], false, loader);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            System.out.print(cnfe.getMessage());
        }
        Object o = Proxy.newProxyInstance(loader, apis, rih);
        return o;
    }
}
