package com.smoner.rpc.demo2.framework.rmi.server;

import com.smoner.rpc.demo2.framework.comn.*;
import com.smoner.rpc.demo2.framework.naming.Context;
import com.smoner.rpc.demo2.framework.util.ClassUtil;
import org.granite.io.FastByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by smoner on 2017/1/27.
 */
public class RMIHandlerImpl implements RMIHandler {
    /* private ModuleNCLocator remoteCtx =this.getRemoteCtx("com.smoner.rpc.demo2.framework.comn.RemoteNCLocator");
     private RemoteNCLocator getRemoteCtx(String classname){
         RemoteNCLocator remoteNCLocator = null;
         try {
             remoteNCLocator = (RemoteNCLocator) ClassUtil.loadClass(classname).newInstance();
         }catch (ClassNotFoundException ex){
             throw  new RuntimeException(classname+" not found !!!! ");
         } catch (InstantiationException e) {
             e.printStackTrace();
         } catch (IllegalAccessException e) {
             e.printStackTrace();
         }
         return remoteNCLocator;
     }
 */
    @Override
    public void handle(RMIContext rmiCtx) throws IOException {
        Result result = new Result();
        byte[] data = readFromNetWrok(rmiCtx);

        try {
            readInvocationInfo(rmiCtx, data);
          /*  boolean legal = isLegalConcurrent(rmiCtx);
            if (!legal) {
                throw new FrameworkSecurityException(" had  over limit concurrent");
            }
            preRemoteProcess();*/
            result.result = invokeBeanMethod(rmiCtx);
        } catch (Throwable ite) {
       /*     Throwable appException = extractException(ite);
            if (verifyThrowable(appException)) {
                result.appexception = appException;
            } else {
                result.appexception = new RuntimeException(ite.getMessage());
            }*/
            result.appexception = new RuntimeException(ite.getMessage());
        }
        data = null;
        rmiCtx.setResult(result);
        try {
            //beforeWriteClient(rmiCtx);
            writeResult(rmiCtx);
        } finally {
            // afterWriteClient(rmiCtx);
        }
    }

    private byte[] readFromNetWrok(RMIContext rmiCtx) throws IOException {
       /* int readLen = -1;
        InputStream in = rmiCtx.getInputStream();
        int total = in.available();
        byte[] bs = new byte[total];
        in.read(bs);
        return bs;*/


        int readLen = -1;
        InputStream in = rmiCtx.getInputStream();
        int len = NetObjectInputStream.readInt(in);

        byte[] bytes = new byte[len];

        readLen = in.read(bytes);

        while (readLen < len) {
            int tmpLen = in.read(bytes, readLen, len - readLen);
            if (tmpLen < 0)
                break;
            readLen += tmpLen;
        }

        if (readLen < len) {
            throw new EOFException("read object error,read " + readLen + " but expect: " + len);
        }

        return bytes;
    }

    public static int readInt(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    private InvocationInfo readInvocationInfo(RMIContext rmiCtx, byte[] bytes) throws IOException,
            ClassNotFoundException {
        //beforeReadInvocationInfo(rmiCtx, bytes);
        try {
            NetObjectInputStream objIn = new NetObjectInputStream(new ByteArrayInputStream(bytes));
            rmiCtx.setCompressed(objIn.isCompressed());
            rmiCtx.setEncrypted(objIn.isEncrypted());
            rmiCtx.setEncryptType(objIn.getEncryptType());
            rmiCtx.setTransKey(objIn.getTransKey());
            InvocationInfo invInfo = null;
            invInfo = (InvocationInfo) objIn.readObject();
            invInfo.fix();
            rmiCtx.setInvocationInfo(invInfo);
            String callId = invInfo.getCallId();
            String ds = invInfo.getUserDataSource();
            if (ds == null)
                ds = "design";
            objIn.close();
            return invInfo;
        } finally {
            //afterReadInvocationInfo(rmiCtx, bytes);
        }
    }


    private Object invokeBeanMethod(RMIContext rmiCtx) throws Throwable {

        InvocationInfo invInfo = rmiCtx.getInvocationInfo();
        String module = invInfo.getModule();
        String service = invInfo.getServiceName();
        String mn = invInfo.getMethodName();
        Class<?>[] pts = invInfo.getParametertypes();
        Object[] ps = invInfo.getParameters();
        Object o = null;
        // beforeInvokeBeanMethod(rmiCtx);
        try {
           /* if (module == null) {
                o = remoteCtx.lookup(service);
            } else {
                Context moduleCtx = ctxMap.get(module);
                if (moduleCtx == null) {
                    Properties props = new Properties();
                    props.put(NCLocator.TARGET_MODULE, module);
                    props.put(NCLocator.LOCATOR_PROVIDER_PROPERTY, "nc.bs.framework.server.ModuleNCLocator");
                    moduleCtx = NCLocator.getInstance(props);
                    ctxMap.put(module, moduleCtx);
                }
                o = moduleCtx.lookup(service);
            }*/

            Map<String, String> interfaceImplMap = new HashMap<String, String>();
            interfaceImplMap.put("com.smoner.rpc.demo2.framework.testclass.itf.ILoginService", "com.smoner.rpc.demo2.framework.testclass.impl.LoginImpl");
            String implname = interfaceImplMap.get(service);
            Class obj = Class.forName(implname);
            o = obj.newInstance();


            Method bm = o.getClass().getMethod(mn, pts);
            bm.setAccessible(true);
            Object result;
            result = bm.invoke(o, ps);
            return result;
        } catch (Exception exp) {
            //Logger.error("component lookup error", exp);
            throw exp;
        } finally {
            // afterInvokeBeanMethod(rmiCtx);
        }
    }


    private void writeResult(RMIContext rmiCtx) throws IOException {
        Result result = rmiCtx.getResult();
        FastByteArrayOutputStream bout = null;

      /*  if (rmiCtx.getResult().appexception != null) {
        }

        try {
            bout = serilaizeObject(rmiCtx);
        } catch (IOException e) {
            result.result = null;
            result.appexception = e;
            bout = serilaizeObject(rmiCtx);
        }

        writeNetwork(rmiCtx, bout.array, 0, bout.length);*/

    }

}
