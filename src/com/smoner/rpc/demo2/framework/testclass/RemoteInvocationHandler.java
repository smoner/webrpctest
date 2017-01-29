package com.smoner.rpc.demo2.framework.testclass;

import com.smoner.rpc.demo2.framework.comn.InvocationInfo;
import com.smoner.rpc.demo2.framework.comn.Result;
import com.smoner.rpc.demo2.framework.exception.ConnectorException;
import com.smoner.rpc.demo2.framework.exception.ConnectorFailException;
import com.smoner.rpc.demo2.framework.exception.ConnectorIOException;
import com.smoner.rpc.demo2.framework.rmi.RemoteChannel;
import com.smoner.rpc.demo2.framework.rmi.RemoteChannelFactory;
import com.smoner.rpc.demo2.framework.rmi.RemoteChannelFactoryManager;
import com.smoner.rpc.demo2.framework.rmi.RemoteUtil;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by smoner on 2017/1/29.
 */
public class RemoteInvocationHandler implements InvocationHandler , RemoteProxy{
    private static final long serialVersionUID = 5133286876175028281L;

    private ComponentMetaVO meta;

    private RemoteAddressSelector ras;

    private Map<String, Object> attributes;

    public RemoteInvocationHandler(ComponentMetaVO meta,
                                   RemoteAddressSelector ras) {
        this.meta = meta;
        this.ras = ras;
        this.attributes = new HashMap<String, Object>();
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String mn = method.getName();
        Class<?>[] ps = method.getParameterTypes();

        if (mn.equals("equals") && ps.length == 1 && ps[0].equals(Object.class)) {
            Object value = args[0];
            if (value == null || !Proxy.isProxyClass(value.getClass())) {
                return Boolean.FALSE;
            }

            Object h = Proxy.getInvocationHandler(value);

            if (!(h instanceof RemoteInvocationHandler)) {
                return Boolean.FALSE;
            } else {
                return meta.equals(((RemoteInvocationHandler) h).meta)
                        && ras.equals(((RemoteInvocationHandler) h).ras);
            }
        } else if (mn.equals("hashCode") && ps.length == 0) {
            return meta.hashCode() + 27 * ras.hashCode();
        } else if (mn.equals("toString") && ps.length == 0) {
            return meta.toString();
        } else if (method.getDeclaringClass() == RemoteProxy.class) {
            return method.invoke(this, args);
        } else {
            return sendRequest(method, args);
        }
    }
    public Object sendRequest(Method method, Object[] args) throws Throwable {
        InvocationInfo ii = newInvocationInfo(method, args);
        Address old = null;
        int retry = 0;
        Throwable error = null;
        do {
            Address target = ras.select();
            if (old != null) {
                System.out.print("connect to: " + old
                        + " failed, now retry connect to: " + target);
                if (old.equals(target)) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }
            }
            try {
                return sendRequest(target, ii, method, args);
            } catch (Exception cfe) {
                retry++;
                old = target;
                error = cfe;
            } finally {
            }
        } while (retry < 1);

        throw error;
    }

    public Object sendRequest(Address target, InvocationInfo ii, Method method,
                              Object[] args) throws Throwable {
        RemoteChannelFactory rcf = RemoteChannelFactoryManager.getDefault()
                .getRemoteChannelFactory(target.getProtocol());
        if (rcf == null) {
            throw new Exception("not support target address:" + target);
        }

        RemoteChannel rc = null;
        Result ret = null;
        try {
            System.out.print("callid=" + ii.getCallId());
            rc = rcf.createRemoteChannel(target);
            rc.init();
            RemoteUtil.writeObject(rc.getOutputStream(), ii);
            ret = (Result) RemoteUtil.readObject(rc.getInputStream(), null);
            if (ret.appexception == null) {
                return ret.result;
            } else {
                throw ret.appexception;
            }
        } catch (ClassNotFoundException e) {
            throw new ConnectorException(rmErrMsg(meta.getName(), method), e);
        } catch (IOException ioe) {
            try {
                rc.processIOException(ioe);
                if (ioe instanceof ConnectException) {
                    ras.fail(target);
                    throw new ConnectorFailException("connect to: " + target
                            + " failed", ioe);
                } else {
                    throw new ConnectorIOException(rmErrMsg(meta.getName(),
                            method), ioe);
                }
            } catch (IOException ioe1) {
                throw new ConnectorIOException(
                        rmErrMsg(meta.getName(), method), ioe1);
            }
        } finally {
            if (rc != null)
                rc.destroy();
        }
    }

    private InvocationInfo newInvocationInfo(Method method, Object[] args) {
        Class<?>[] myclasses = null;
        myclasses = method.getParameterTypes();
        //InvocationInfoProxy iip = InvocationInfoProxy.getInstance();
        InvocationInfo ii = new InvocationInfo(meta.getModule(),
                meta.getName(), method.getName(), myclasses, args,
                "127.0.0.1");
        /*ii.setGroupId(iip.getGroupId());
        ii.setLangCode(iip.getLangCode());
        ii.setSysid(iip.getSysid());
        ii.setUserId(iip.getUserId());
        ii.setUserDataSource(iip.getUserDataSource());
        ii.setGroupNumber(iip.getGroupNumber());
        ii.setBizCenterCode(iip.getBizCenterCode());*/
        String callId = null;
        if (callId == null) {
            callId = System.currentTimeMillis() + "-"
                    + new Random().nextInt(10000);
        }
        ii.setCallId(callId);

        String callPath = null;
        if (callPath == null) {
            callPath = "";
        }
        callPath = callPath + "/" + "__client";
        ii.setCallPath(callPath);

        ii.setMetodName(method.getName());
        return ii;
    }


    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    public ComponentMetaVO getComponentMetaVO() {
        return meta;
    }

    @Override
    public int getRetryMax() {
        return 0;
    }

    @Override
    public void setRetryMax(int retryMax) {

    }

    @Override
    public long getRetryInterval() {
        return 0;
    }

    @Override
    public void setRetryInterval(long l) {

    }

    @Override
    public void setRemoteAddressSelector(RemoteAddressSelector ras) {

    }

    @Override
    public RemoteAddressSelector getRemoteAddressSelector() {
        return null;
    }
    private String rmErrMsg(String name, Method method) {
        StringBuffer sb = new StringBuffer("remote request error:");
        sb.append(name);
        sb.append('/').append(method.getDeclaringClass()).append('.')
                .append(method.getName());
        return sb.toString();

    }
}
