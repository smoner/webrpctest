package com.smoner.rpc.demo3.myframework.server;

import com.smoner.rpc.demo2.framework.comn.NetObjectInputStream;
import com.smoner.rpc.demo2.framework.comn.NetObjectOutputStream;
import com.smoner.rpc.demo3.myframework.client.Result;
import com.smoner.rpc.demo3.myframework.pub.InvocationInfo;
import org.granite.io.FastByteArrayOutputStream;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RMIHandlerImpl {
    public void handle(RMIContext rmiCtx) throws IOException {
        Result result = new Result();
        //byte[] data = readFromNetWrok(rmiCtx);
        try {
           // readInvocationInfo(rmiCtx, data);
            readInvocationInfo_new(rmiCtx);
            result.result = invokeBeanMethod(rmiCtx);
        } catch (Throwable ite) {
            result.appexception = new RuntimeException(ite.getMessage());
        }
        rmiCtx.setResult(result);
        try {
            writeResult(rmiCtx);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    private byte[] readFromNetWrok(RMIContext rmiCtx) throws IOException {
        int readLen = -1;
        InputStream in = rmiCtx.getInputStream();

//        ObjectInputStream objectInputStream = new ObjectInputStream(in);
//        Object data = null;
//        try {
//            data = objectInputStream.readObject();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = in.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
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
        InvocationInfo invInfo = null;
        try {
            NetObjectInputStream objIn = new NetObjectInputStream(new ByteArrayInputStream(bytes));
            rmiCtx.setCompressed(objIn.isCompressed());
            rmiCtx.setEncrypted(objIn.isEncrypted());
            rmiCtx.setEncryptType(objIn.getEncryptType());
            rmiCtx.setTransKey(objIn.getTransKey());
            invInfo = (InvocationInfo) objIn.readObject();
            rmiCtx.setInvocationInfo(invInfo);
            String callId = invInfo.getCallId();
            String ds = invInfo.getUserDataSource();
            if (ds == null)
                ds = "design";
            objIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.print(" over is over ---------------------------------!");
        }
        return invInfo;
    }


    private InvocationInfo readInvocationInfo_new(RMIContext rmiCtx) throws IOException,
            ClassNotFoundException {
        InputStream in = rmiCtx.getInputStream();
        InvocationInfo invInfo = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            try {
                invInfo = (InvocationInfo)  objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            rmiCtx.setInvocationInfo(invInfo);
            String callId = invInfo.getCallId();
            String ds = invInfo.getUserDataSource();
            if (ds == null)
                ds = "design";
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.print(" over is over ---------------------------------!");
        }
        return invInfo;
    }


    private Object invokeBeanMethod(RMIContext rmiCtx) throws Throwable {
        InvocationInfo invInfo = rmiCtx.getInvocationInfo();
        String module = invInfo.getModule();
        String service = invInfo.getServiceName();
        String mn = invInfo.getMethodName();
        Class<?>[] pts = invInfo.getParametertypes();
        Object[] ps = invInfo.getParameters();
        Object o = null;
        try {
            Map<String, String> interfaceImplMap = new HashMap<String, String>();
            interfaceImplMap.put("com.smoner.rpc.demo3.test.ILoginService", "com.smoner.rpc.demo3.test.LoginImpl");
            String implname = interfaceImplMap.get(service);
            Class obj = Class.forName(implname);
            o = obj.newInstance();
            Method bm = o.getClass().getMethod(mn, pts);
            bm.setAccessible(true);
            Object result;
            result = bm.invoke(o, ps);
            return result;
        } catch (Exception exp) {
            throw exp;
        } finally {
        }
    }


    private void writeResult(RMIContext rmiCtx) throws IOException {
        Result result = rmiCtx.getResult();
        FastByteArrayOutputStream bout = null;

        if (rmiCtx.getResult().appexception != null) {
        }

        try {
            bout = serilaizeObject(rmiCtx);
        } catch (IOException e) {
            result.result = null;
            result.appexception = e;
            bout = serilaizeObject(rmiCtx);
        }
        writeNetwork(rmiCtx, bout.array, 0, bout.length);
    }

    private FastByteArrayOutputStream serilaizeObject(RMIContext rmiCtx) throws IOException {
        FastByteArrayOutputStream bout = null;
        NetObjectOutputStream objOut = null;
        try {
            bout = new FastByteArrayOutputStream();
            objOut = new NetObjectOutputStream(bout, rmiCtx.isCompressed(), rmiCtx.isEncrypted(),
                    rmiCtx.getEncryptType(), rmiCtx.getTransKey());
            objOut.writeObject(rmiCtx.getResult());
            return bout;
        } finally {
            if (objOut != null)
                objOut.close();
        }
    }

    private void writeNetwork(RMIContext rmiCtx, byte[] resultData, int from, int length) throws IOException {
        try {
            rmiCtx.setOutputContentLength(length + 4);
            NetObjectOutputStream.writeInt(rmiCtx.getOutputStream(), length);
            rmiCtx.getOutputStream().write(resultData, from, length);
        } finally {
            rmiCtx.getOutputStream().flush();
        }
    }
}