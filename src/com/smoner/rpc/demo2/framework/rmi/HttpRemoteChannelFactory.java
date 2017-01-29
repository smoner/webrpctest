package com.smoner.rpc.demo2.framework.rmi;

import com.smoner.rpc.demo2.framework.exception.FrameworkRuntimeException;
import com.smoner.rpc.demo2.framework.testclass.Address;

import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by smoner on 2017/1/29.
 */

public class HttpRemoteChannelFactory implements RemoteChannelFactory {

    static {
        CookieHandler.setDefault(HttpRemoteChannel.newCookieManager());
    }

    public HttpRemoteChannelFactory() {
        CookieHandler.setDefault(HttpRemoteChannel.newCookieManager());
    }

    private Proxy proxy;

    public boolean isKeepAlive() {
        String ka = System.getProperty("http.keepAlive");
        if (ka != null) {
            return Boolean.valueOf(ka);
        }
        return true;
    }

    public void setKeepAlive(boolean keepAlive) {
        System.setProperty("http.keepAlive", "" + keepAlive);
    }

    public int getMaxConnections() {
        String mc = System.getProperty("http.maxConnections");
        if (mc != null) {
            return Integer.parseInt(mc);
        } else {
            return 5;
        }
    }

    public void setMaxConnections(int maxConnections) {
        System.setProperty("http.maxConnections", "" + maxConnections);
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public RemoteChannel createRemoteChannel(Address addr) {
        URL url;
        try {
            url = addr.toURL();
        } catch (MalformedURLException e) {
            throw new FrameworkRuntimeException("error when create channel", e);
        }
        return new HttpRemoteChannel(url, proxy);
    }

}