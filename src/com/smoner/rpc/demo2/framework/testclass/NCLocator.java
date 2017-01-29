package com.smoner.rpc.demo2.framework.testclass;

import com.smoner.rpc.demo2.framework.testclass.itf.ILoginService;

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
        RemoteAddressSelector ras = new GroupBasedRemoteAddressSelector(address, null);
        so = RemoteProxyFactory.getDefault().createRemoteProxy(NCLocator.class.getClassLoader(), metaVO, ras);
        proxyMap.put(metaVO.getName(), so);
        return so;
    }
}
