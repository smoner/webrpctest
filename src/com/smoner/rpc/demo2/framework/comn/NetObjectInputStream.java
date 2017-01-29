package com.smoner.rpc.demo2.framework.comn;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class NetObjectInputStream extends ObjectInputStream {

    private int bufferSize;

    private boolean compressed =false;

    private boolean encrypted;

    private int encryptType;

    private ObjectInputStream objIn;

    private CountInputStream statisticStream;

    private byte[] transKey = new byte[1];

    public NetObjectInputStream(InputStream in) throws IOException {
        this(in, NetStreamConstants.NC_STREAM_BUFFER_SIZE);
    }

    public NetObjectInputStream(InputStream in, int bufferSize) throws IOException {
        this(in, bufferSize, new NCObjectResolver());
    }

    public NetObjectInputStream(InputStream in, int bufferSize, ObjectResolver resolver) throws IOException {
        super();
        init(in, bufferSize, resolver);
    }

    public void init(InputStream in, int bufferSize, ObjectResolver resolver) throws IOException {
        this.bufferSize = bufferSize;
        objIn = new NCObjectInputStream(new NCInputStream(in), resolver);

        int len = objIn.readInt();

        if (len > 0) {
            byte[] token = new byte[len];
            int k = 0;
            do {
                int l = objIn.read(token, k, (len - k));
                if (l == len) {
                    break;
                }

                if (l == -1) {
                    break;
                }

                k += l;
            } while (true);
            NetStreamContext.setToken(token);
        } else {
            // 传过来的token值为空,由于调用第三方的代码时没有token,所以不能直接清空token
            //Logger.warn("NetObjectInputStream lost token!");
            // NetStreamContext.setToken(null);
        }

    }

    private static class NCObjectInputStream extends ObjectInputStream {
        private ObjectResolver resolver;

        public NCObjectInputStream(InputStream in, ObjectResolver resolver) throws IOException {
            super(in);
            this.resolver = resolver;
            if (this.resolver != null) {
                enableResolveObject(true);
            }

        }

        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            ClassLoader loader = (ClassLoader) System.getProperties().get("nc.classLoader");
            if (loader == null) {
                return super.resolveClass(desc);
            } else {
                String name = desc.getName();
                try {
                    return Class.forName(name, false, loader);
                } catch (Exception e) {
                   // Debug.error("ERROR___NetObjectInputStream: " + e.getMessage());
                    return super.resolveClass(desc);
                }
            }
        }

        protected Object resolveObject(Object obj) throws IOException {
            if (resolver == null) {
                return obj;
            } else {
                return resolver.resolveObject(obj);
            }
        }
    }

    private class NCInputStream extends InputStream {

        private InputStream input;
        private IFastIOStream ioStream;

        public NCInputStream(InputStream inputStream) throws IOException {
            input = new BufferedInputStream(inputStream, bufferSize * 4);
            if ("true".equals(System
                    .getProperty("nc.stream.statistic"))) {
                input = statisticStream = new CountInputStream(input);
            }

            int magicCode = input.read() | input.read() << 8 | input.read() << 16;

            if (magicCode != 0x897172) {
                throw new IOException("Illegal NC data: May be malicious attack system or Version is not compatible(V5x). VER");
            }

            int header = input.read();

            encrypted = (header & 0x1) != 0;

            compressed = (header & 0x2) != 0;

            encryptType = IOStreamFactory.genEncryptType(header);

            if (encrypted) {
                ioStream = IOStreamFactory.getFastIOStream(encryptType);
                input = ioStream.createInputStream(input, transKey);
            }

            if (compressed) {
                input = new InflaterInputStream(input, new Inflater(), bufferSize);
                input = new BufferedInputStream(input, bufferSize);
            }
        }

        public int read() throws IOException {
            return input.read();
        }

        public int read(byte[] b, int offset, int len) throws IOException {
            return input.read(b, offset, len);
        }

        public void close() throws IOException {
            if (input != null)
                input.close();
            input = null;
        }

    }

    public byte[] getTransKey() {
        return transKey;
    }

    public void setTransKey(byte[] transKey) {
        this.transKey = transKey;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public int available() throws IOException {
        return objIn.available();
    }

    public void close() throws IOException {
        objIn.close();
    }

    public void defaultReadObject() throws IOException, ClassNotFoundException {
        objIn.defaultReadObject();
    }

    public void mark(int readlimit) {
        objIn.mark(readlimit);
    }

    public boolean markSupported() {
        return objIn.markSupported();
    }

    public int read() throws IOException {
        return objIn.read();
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        return objIn.read(buf, off, len);
    }

    public int read(byte[] b) throws IOException {
        return objIn.read(b);
    }

    public boolean readBoolean() throws IOException {
        return objIn.readBoolean();
    }

    public byte readByte() throws IOException {
        return objIn.readByte();
    }

    public char readChar() throws IOException {
        return objIn.readChar();
    }

    public double readDouble() throws IOException {
        return objIn.readDouble();
    }

    public GetField readFields() throws IOException, ClassNotFoundException {
        return objIn.readFields();
    }

    public float readFloat() throws IOException {
        return objIn.readFloat();
    }

    public void readFully(byte[] buf, int off, int len) throws IOException {
        objIn.readFully(buf, off, len);
    }

    public void readFully(byte[] buf) throws IOException {
        objIn.readFully(buf);
    }

    public int readInt() throws IOException {
        return objIn.readInt();
    }

    @Deprecated
    public String readLine() throws IOException {
        return objIn.readLine();
    }

    public long readLong() throws IOException {
        return objIn.readLong();
    }

    public Object readObjectOverride() throws ClassNotFoundException, IOException {
        return objIn.readObject();

    }

    public short readShort() throws IOException {
        return objIn.readShort();
    }

    public Object readUnshared() throws IOException, ClassNotFoundException {
        return objIn.readUnshared();
    }

    public int readUnsignedByte() throws IOException {
        return objIn.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return objIn.readUnsignedShort();
    }

    public String readUTF() throws IOException {
        return objIn.readUTF();
    }

    public void registerValidation(ObjectInputValidation obj, int prio) throws NotActiveException, InvalidObjectException {
        objIn.registerValidation(obj, prio);
    }

    public long skip(long n) throws IOException {
        return objIn.skip(n);
    }

    public int skipBytes(int len) throws IOException {
        return objIn.skipBytes(len);
    }

    public void reset() throws IOException {
        objIn.reset();
    }

    public static Object readObject(InputStream in, boolean[] retValue) throws IOException, ClassNotFoundException {
        BufferedInputStream bin = new BufferedInputStream(in);
        int len = readInt(bin);

        byte[] bytes = new byte[len];

        int readLen = bin.read(bytes);

        while (readLen < len) {
            int tmpLen = bin.read(bytes, readLen, len - readLen);
            if (tmpLen < 0)
                break;
            readLen += tmpLen;
        }

        if (readLen < len) {
            throw new EOFException("ReadObject EOF error readLen: " + readLen + " expected: " + len);
        }

        NetObjectInputStream objIn = new NetObjectInputStream(new ByteArrayInputStream(bytes));

        if (retValue != null) {
            retValue[0] = objIn.isCompressed();
            retValue[1] = objIn.isEncrypted();
        }

        return objIn.readObject();

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

    public long getNetBytes() {
        return statisticStream == null ? -1 : statisticStream.getCount();
    }

}