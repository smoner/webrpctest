package com.smoner.rpc.demo3.myframework.client;

import com.smoner.rpc.demo2.framework.exception.ConnectorException;
import com.smoner.rpc.demo2.framework.exception.ConnectorFailException;
import com.smoner.rpc.demo2.framework.exception.ConnectorIOException;
import com.smoner.rpc.demo3.myframework.pub.InvocationInfo;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by smoner on 2017/1/29.
 */
public class RemoteInvocationHandler implements InvocationHandler {
    private static final long serialVersionUID = 5133284876175028281L;
    private ComponentMetaVO meta;
    private Address ras;
    private Map<String, Object> attributes;

    public RemoteInvocationHandler(ComponentMetaVO meta,
                                   Address ras) {
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
            Address target = ras;
            if (old != null) {
                System.out.print("connect to: " + old
                        + " failed, now retry connect to: " + target);
                if (old.equals(target)) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
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
//        RemoteChannel rc = null;
//        Result ret = null;
//        try {
//            rc = createRemoteChannel(target);
//            rc.init();
//            RemoteUtil.writeObject(rc.getOutputStream(), ii);
//            ret = (Result) RemoteUtil.readObject(rc.getInputStream(), null);
//            if (ret.appexception == null) {
//                return ret.result;
//            } else {
//                throw ret.appexception;
//            }
//        } catch (ClassNotFoundException e) {
//            throw new ConnectorException(rmErrMsg(meta.getName(), method), e);
//        } catch (IOException ioe) {
//            try {
//                rc.processIOException(ioe);
//                if (ioe instanceof ConnectException) {
//                    throw new ConnectorFailException("connect to: " + target
//                            + " failed", ioe);
//                } else {
//                    throw new ConnectorIOException(rmErrMsg(meta.getName(),
//                            method), ioe);
//                }
//            } catch (IOException ioe1) {
//                throw new ConnectorIOException(
//                        rmErrMsg(meta.getName(), method), ioe1);
//            }
//        } finally {
//            if (rc != null)
//                rc.destroy();
//        }


        Result ret = null;
        HttpURLConnection conn = (HttpURLConnection) target.toURL().openConnection();
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在 http正文内，因此需要设为true, 默认情况下是false;
        conn.setDoOutput(true);
        //设定传送的内容类型是可序列化的Java对象 (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
        conn.setRequestProperty("Content-type", "application/x-java-serialized-object");
        //配置必须要在connect之前完成，
        conn.connect();
        RemoteUtil.writeObject(conn.getOutputStream(), ii);
        ret = (Result) RemoteUtil.readObject(conn.getInputStream(), null);
        return ret ;
    }

    public RemoteChannel createRemoteChannel(Address addr) {
        URL url = null;
        try {
            url = addr.toURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HttpRemoteChannel(url, null);
    }

    private InvocationInfo newInvocationInfo(Method method, Object[] args) {
        Class<?>[] myclasses = null;
        myclasses = method.getParameterTypes();
        InvocationInfo ii = new InvocationInfo(meta.getModule(),
                meta.getName(), method.getName(), myclasses, args,
                "127.0.0.1");
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

    private String rmErrMsg(String name, Method method) {
        StringBuffer sb = new StringBuffer("remote request error:");
        sb.append(name);
        sb.append('/').append(method.getDeclaringClass()).append('.')
                .append(method.getName());
        return sb.toString();
    }
}
