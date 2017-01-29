package com.smoner.rpc.demo2.framework.comn;

import java.util.Properties;

/**
 * Created by smoner on 2017/1/27.
 */
public class InvocationInfoProxy {
    private ThreadLocal<Properties> invProperties = new ThreadLocal<Properties>() {
        protected Properties initialValue() {
            return new Properties();
        }
    };
    private static InvocationInfoProxy cen = new InvocationInfoProxy();

    private ThreadLocal<InvocationInfo> infoLocal = new ThreadLocal<InvocationInfo>();

    public static InvocationInfoProxy getInstance() {
        return cen;
    }

    private InvocationInfoProxy() {
        super();
    }

    protected InvocationInfo getInvocationInfo() {
        return (InvocationInfo) infoLocal.get();
    }

    protected void setInvocationInfo(InvocationInfo info) {
        infoLocal.set(info);
    }

    public void setProperty(String key, String value) {
        String oldValue = this.invProperties.get().getProperty(key);
        if (value == null) {
            if (oldValue != null) {
            }
            this.invProperties.get().remove(key);
        } else {
            if (oldValue != null && !oldValue.equals(value)) {
            }
            this.invProperties.get().setProperty(key, value);
        }
    }

    public String getProperty(String key) {
        return invProperties.get().getProperty(key);
    }
}
