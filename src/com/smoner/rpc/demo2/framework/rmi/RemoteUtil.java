package com.smoner.rpc.demo2.framework.rmi;

import com.smoner.rpc.demo2.framework.comn.NetObjectInputStream;
import com.smoner.rpc.demo2.framework.comn.NetObjectOutputStream;
import com.smoner.rpc.demo2.framework.comn.NetStreamConstants;
import com.smoner.rpc.demo2.framework.testclass.Address;
import com.smoner.rpc.demo2.framework.testclass.ComponentMetaVO;
import com.smoner.rpc.demo2.framework.testclass.RemoteProxyFactory;

import java.io.*;

/**
 * Created by smoner on 2017/1/29.
 */
public class RemoteUtil {

    public static void writeObject(OutputStream output, Object obj)
            throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
//            NetObjectOutputStream objOut = new NetObjectOutputStream(bout,
//                    NetStreamConstants.EnctyptType.DynamicAES);
            NetObjectOutputStream objOut = new NetObjectOutputStream(bout,false,true);

            objOut.writeObject(obj);
            objOut.finish();
            objOut.flush();
            writeInt(output, bout.size());
        } finally {
        }
        try {
            bout.writeTo(output);
            output.flush();
        } finally {
        }
    }

    public static ByteArrayOutputStream convertObjectToBytes(Object obj,
                                                             boolean compressed, boolean encrypted) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            NetObjectOutputStream objOut = new NetObjectOutputStream(bout,
                    compressed, encrypted,
                    NetStreamConstants.EnctyptType.DynamicAES);
            objOut.writeObject(obj);
            objOut.finish();
            objOut.flush();
        } finally {
        }
        return bout;
    }

    public static void writeInt(OutputStream output, int v) throws IOException {
        byte bytes[] = new byte[4];
        bytes[0] = (byte) ((v >>> 24) & 0xFF);
        bytes[1] = (byte) ((v >>> 16) & 0xFF);
        bytes[2] = (byte) ((v >>> 8) & 0xFF);
        bytes[3] = (byte) ((v >>> 0) & 0xFF);

        output.write(bytes);
    }

    public static Object readObject(InputStream in, boolean[] retValue)
            throws IOException, ClassNotFoundException {

        BufferedInputStream bin = new BufferedInputStream(in);
        byte[] bytes = null;
        try {
            int len = readInt(bin);

            bytes = new byte[len];

            int readLen = bin.read(bytes);

            while (readLen < len) {
                int tmpLen = bin.read(bytes, readLen, len - readLen);
                if (tmpLen < 0)
                    break;
                readLen += tmpLen;
            }

            if (readLen < len) {
                throw new EOFException("ReadObject EOF error readLen: "
                        + readLen + " expected: " + len);
            }
        } finally {
        }


        try {
            NetObjectInputStream objIn = new NetObjectInputStream(
                    new ByteArrayInputStream(bytes));

            if (retValue != null) {
                retValue[0] = objIn.isCompressed();
                retValue[1] = objIn.isEncrypted();
            }

            return objIn.readObject();
        } finally {
        }

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

    public Object[] createRemoteProxy(ClassLoader loader,
                                      ComponentMetaVO metaVO, Address[] address) {
        Object[] remotes = new Object[address.length];
        for (int i = 0; i < address.length; i++) {
            remotes[i] = RemoteProxyFactory.getDefault()
                    .createRemoteProxy(loader, metaVO,
                            new SimpleRemoteAddressSelector(address[i]));
        }

        return remotes;

    }
}
